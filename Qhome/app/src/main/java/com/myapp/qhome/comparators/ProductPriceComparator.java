package com.myapp.qhome.comparators;

import com.myapp.qhome.commons.Commons;
import com.myapp.qhome.models.Product;

import java.util.Comparator;

public class ProductPriceComparator implements Comparator<Product> {
    @Override
    public int compare(Product product1, Product product2) {
        if(Commons.priceSort == 1) return (int) (product1.getPrice() - product2.getPrice());
        else if(Commons.priceSort == 2) return (int) (product2.getPrice() - product1.getPrice());
        else return 0;
    }
}
