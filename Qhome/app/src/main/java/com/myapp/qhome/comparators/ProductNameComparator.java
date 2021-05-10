package com.myapp.qhome.comparators;

import com.myapp.qhome.commons.Commons;
import com.myapp.qhome.models.Product;

import java.util.Comparator;

public class ProductNameComparator implements Comparator<Product> {

    @Override
    public int compare(Product product1, Product product2) {
        if(Commons.nameSort == 1 && Commons.lang.equals("en"))return product1.getName().compareToIgnoreCase(product2.getName());
        else if(Commons.nameSort == 2 && Commons.lang.equals("en"))return product2.getName().compareToIgnoreCase(product1.getName());
        else if(Commons.nameSort == 1 && Commons.lang.equals("ar"))return product1.getAr_name().compareToIgnoreCase(product2.getAr_name());
        else if(Commons.nameSort == 2 && Commons.lang.equals("ar"))return product2.getAr_name().compareToIgnoreCase(product1.getAr_name());
        else return 0;
    }
}