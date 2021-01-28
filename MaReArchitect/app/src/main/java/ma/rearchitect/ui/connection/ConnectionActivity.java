package opswat.com.flow.connection;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import opswat.com.R;
import opswat.com.adapter.ConnectionAdapter;
import opswat.com.flow.base.BaseActivity;
import opswat.com.mvp.MvpPresenter;
import opswat.com.network.model.connection.Connection;

public class ConnectionActivity extends BaseActivity implements IConnectionView{
    private IConnectionPresenter presenter = new ConnectionPresenterIml();

    private RecyclerView recyclerViewScanning;
    private RecyclerView recyclerViewClean;
    private RecyclerView recyclerViewSuspect;
    private RecyclerView recyclerViewDirty;

    private ConnectionAdapter scanningAdapter = new ConnectionAdapter(ConnectionAdapter.TYPE_IP_DETAIL);
    private ConnectionAdapter cleanAdapter = new ConnectionAdapter(ConnectionAdapter.TYPE_IP_DETAIL);
    private ConnectionAdapter suspectAdapter = new ConnectionAdapter(ConnectionAdapter.TYPE_IP_DETAIL);
    private ConnectionAdapter dirtyAdapter = new ConnectionAdapter(ConnectionAdapter.TYPE_IP_DETAIL);

    @Override
    protected MvpPresenter getPresenter() {
        return presenter;
    }

    @Override
    protected Integer getBtnMenuId() {
        return R.id.connections_right_menu;
    }

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_connection;
    }

    @Override
    protected Integer getSlideMenuId() {
        return R.id.connections_slide_menu;
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, ConnectionActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void initialized() {
        initRecycleView();
        handleIpConnection();

        findViewById(R.id.connections_btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backHomePage();
            }
        });

        setTextView(R.id.connections_tv_title_name, getString(R.string.connections));
    }

    private void backHomePage() {
        this.finish();
    }

    @Override
    public void handleIpConnection() {
        super.handleIpConnection();
        bindingListConnection(MAApplication.getInstance().connections);
    }

    private void bindingListConnection(List<Connection> connections) {
        List<Connection> scanningIps = new ArrayList<>(), cleanIps = new ArrayList<>(),
                suspectIps = new ArrayList<>(), dirtyIps = new ArrayList<>();
        for (Connection connection: connections) {
            if (!connection.isScanned()) {
                scanningIps.add(connection);
                continue;
            }

            int numDetected = connection.getLookupResults() != null ? connection.getLookupResults().getDetectedBy() : 0;
            if (numDetected >= 3) {
                dirtyIps.add(connection);
            } else if (numDetected >= 1) {
                suspectIps.add(connection);
            } else {
                cleanIps.add(connection);
            }
        }

        recyclerViewScanning.setVisibility(scanningIps.isEmpty()? View.GONE: View.VISIBLE);
        findViewById(R.id.connections_tv_scanning).setVisibility(scanningIps.isEmpty()? View.GONE: View.VISIBLE);
        findViewById(R.id.connections_line_scanning).setVisibility(scanningIps.isEmpty()? View.GONE: View.VISIBLE);
        scanningAdapter.setListIps(scanningIps);

        recyclerViewClean.setVisibility(cleanIps.isEmpty()? View.GONE: View.VISIBLE);
        findViewById(R.id.connections_tv_clean).setVisibility(cleanIps.isEmpty()? View.GONE: View.VISIBLE);
        findViewById(R.id.connections_line_clean).setVisibility(cleanIps.isEmpty()? View.GONE: View.VISIBLE);
        cleanAdapter.setListIps(cleanIps);

        recyclerViewSuspect.setVisibility(suspectIps.isEmpty()? View.GONE: View.VISIBLE);
        findViewById(R.id.connections_tv_suspect).setVisibility(suspectIps.isEmpty()? View.GONE: View.VISIBLE);
        findViewById(R.id.connections_line_suspect).setVisibility(suspectIps.isEmpty()? View.GONE: View.VISIBLE);
        suspectAdapter.setListIps(suspectIps);

        recyclerViewDirty.setVisibility(dirtyIps.isEmpty()? View.GONE: View.VISIBLE);
        findViewById(R.id.connections_tv_dirty).setVisibility(dirtyIps.isEmpty()? View.GONE: View.VISIBLE);
        findViewById(R.id.connections_line_dirty).setVisibility(dirtyIps.isEmpty()? View.GONE: View.VISIBLE);
        dirtyAdapter.setListIps(dirtyIps);
    }

    private void initRecycleView() {
        recyclerViewScanning = findViewById(R.id.connections_recycleView_scanning);
        recyclerViewClean = findViewById(R.id.connections_recycleView_clean);
        recyclerViewSuspect = findViewById(R.id.connections_recycleView_suspect);
        recyclerViewDirty = findViewById(R.id.connections_recycleView_dirty);

        RecyclerView[] recycleViewArr = {recyclerViewScanning, recyclerViewClean, recyclerViewSuspect, recyclerViewDirty};
        ConnectionAdapter[] connectionAdapterArr = {scanningAdapter, cleanAdapter, suspectAdapter, dirtyAdapter};
        LinearLayoutManager[] layoutManagerArr = {new LinearLayoutManager(this), new LinearLayoutManager(this),
                new LinearLayoutManager(this), new LinearLayoutManager(this)};

        for (int idx = 0; idx < recycleViewArr.length; idx++) {
            recycleViewArr[idx].setLayoutManager(layoutManagerArr[idx]);
            recycleViewArr[idx].setAdapter(connectionAdapterArr[idx]);
            recycleViewArr[idx].setNestedScrollingEnabled(false);
        }
    }
}
