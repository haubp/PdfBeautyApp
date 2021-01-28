package opswat.com.network.model.device.application;

/**
 * Created by H. Len Vo on 9/11/18.
 */
public class Product {
    private String name;
    private String version;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
