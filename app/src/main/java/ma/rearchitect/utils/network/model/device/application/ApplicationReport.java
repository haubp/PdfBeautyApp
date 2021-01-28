package opswat.com.network.model.device.application;

/**
 * Created by H. Len Vo on 9/11/18.
 */
public class ApplicationReport {
    private Product product;

    public ApplicationReport() {
        product = new Product();
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    @Override
    public String toString() {
        return "ApplicationReport{" +
                "product=" + product +
                '}';
    }
}
