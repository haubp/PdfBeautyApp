/*
 * Copyright (C) 2017 MINDORKS NEXTGEN PRIVATE LIMITED
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://mindorks.com/license/apache-v2
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package opswat.com.view.viewholder;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import opswat.com.util.ColorUtil;

/**
 * Created by LenVo on 7/15/18.
 */

public abstract class BaseViewHolder extends RecyclerView.ViewHolder {

    protected View itemView;
    protected Context context;

    public BaseViewHolder(View itemView, Context context) {
        super(itemView);
        this.itemView = itemView;
        this.context = context;
    }

    protected void setText(@IdRes int tvId, String text) {
        TextView textView = itemView.findViewById(tvId);
        textView.setText(text);
    }

    protected void setTextColor(@IdRes int tvId, int colorId) {
        TextView textView = itemView.findViewById(tvId);
        textView.setTextColor(ColorUtil.getColor(context, colorId));
    }

    protected void setImageView(@IdRes int imgId, int resId) {
        ImageView imageView = itemView.findViewById(imgId);
        imageView.setImageDrawable(context.getDrawable(resId));
    }
}
