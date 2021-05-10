package com.myapp.qhome.commons;

import android.widget.TextView;

import com.myapp.qhome.classes.OrderStatus;
import com.myapp.qhome.main.CAdminActivity;
import com.myapp.qhome.main.CategoryActivity;
import com.myapp.qhome.main.CheckoutActivity;
import com.myapp.qhome.main.ChooseLangActivity;
import com.myapp.qhome.main.HomeActivity;
import com.myapp.qhome.main.MyStoreDetailActivity;
import com.myapp.qhome.main.OrdersActivity;
import com.myapp.qhome.main.PhoneVerifyActivity;
import com.myapp.qhome.main.ProductDetailActivity;
import com.myapp.qhome.main.ReceivedOrdersActivity;
import com.myapp.qhome.main.RegisterStoreActivity;
import com.myapp.qhome.main.ShippingAddressActivity;
import com.myapp.qhome.main.StoreDetailActivity;
import com.myapp.qhome.models.Address;
import com.myapp.qhome.models.CartItem;
import com.myapp.qhome.models.Order;
import com.myapp.qhome.models.OrderItem;
import com.myapp.qhome.models.Phone;
import com.myapp.qhome.models.Product;
import com.myapp.qhome.models.Store;
import com.myapp.qhome.models.User;

import java.util.ArrayList;

public class Commons {
    public static String lang = "en";

    public static HomeActivity homeActivity = null;
    public static CategoryActivity categoryActivity = null;

    public static User thisUser = new User();
    public static User user = new User();

    public static ArrayList<Store> myStores = new ArrayList<>();
    public static ArrayList<Store> stores = new ArrayList<>();
    public static Store store = new Store();
    public static Product product = new Product();
    public static StoreDetailActivity storeDetailActivity = null;
    public static MyStoreDetailActivity myStoreDetailActivity = null;

    public static String IMEI = "";
    public static ArrayList<CartItem> cartItems = new ArrayList<>();
    public static int cartItemsCount = 0;

    public static Product cartProduct = new Product();

    public static ArrayList<Phone> phones = new ArrayList<>();
    public static ArrayList<Address> addresses = new ArrayList<>();

    public static int phoneId = 0;
    public static int addrId = 0;

    public static CheckoutActivity checkoutActivity = null;
    public static double totalPrice = 0.0d;

    public static Order order = new Order();
    public static OrdersActivity ordersActivity = null;
    public static ReceivedOrdersActivity receivedOrdersActivity = null;
    public static OrderItem orderItem = new OrderItem();

    public static OrderStatus orderStatus = new OrderStatus();

    public static ArrayList<Store> favStores = new ArrayList<>();
    public static boolean isNotificationEnabled = false;

    public static int minPriceVal = 0;
    public static int maxPriceVal = Constants.MAX_PRODUCT_PRICE;

    public static int priceSort = 0;
    public static int nameSort = 0;

    public static CAdminActivity cAdminActivity = null;
    public static RegisterStoreActivity registerStoreActivity = null;
}































