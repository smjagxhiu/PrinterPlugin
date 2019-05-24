package cordova.plugin.printerplugin;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.os.RemoteException;

import com.huiyi.ypos.usdk.aidl.AidlBarcodeCallBack;
import com.huiyi.ypos.usdk.aidl.AidlDeviceService;
import com.huiyi.ypos.usdk.aidl.AidlPrinter;
import com.huiyi.ypos.usdk.aidl.AidlPrinterListener;
import com.huiyi.ypos.usdk.aidl.AidlScan;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PrinterPlugin extends CordovaPlugin {

    static int MAX = 33;
    static String data = "[{  " + "   \"id\":414," + "   \"price\":61.93, " + "   \"tax\":141.62,"
            + "   \"vat\":203.55," + "   \"status\":\"payed\"," + "   \"discount\":0,"
            + "   \"number\":\"00126-05-2019\"," + "   \"qrcode_path\":\"invoices-qrcode\\/00126-05-2019.png\","
            + "   \"company\": {" + "	\"company_name\" : \"ABC\"," + "   	\"address\" : \"xxx yyy\","
            + "   	\"city\" : \"Prishtine\"," + "   	\"country\" : \"Kosovo\"," + "	\"phone\" : \"044 2222222\","
            + "	\"web\"   : \"www.google.com\"," + "	\"fiscal_num\" : \"121213131343\"" + "   },  		"
            + "   \"items\":[  " + "      {  " + "         \"id\":362," + "         \"count\":1,"
            + "         \"price\":2.5," + "         \"attributes\":[  " + "            {  "
            + "               \"id\":208," + "               \"price\":\"2.10\"," + "               \"attribute\":{  "
            + "                  \"id\":20," + "                  \"name\":\"peperoni\","
            + "                  \"price\":2," + "                  \"tax\":2," + "                  \"vat\":2"
            + "               }" + "            }," + "            {  " + "               \"id\":209,"
            + "               \"price\":\"24.00\"," + "               \"attribute\":{  "
            + "                  \"id\":22," + "                  \"name\":\"mushrooms\","
            + "                  \"price\":30," + "                  \"tax\":20," + "                  \"vat\":30"
            + "               }" + "            }," + "            {  " + "               \"id\":210,"
            + "               \"price\":\"0.00\"," + "               \"attribute\":{  " + "                  \"id\":25,"
            + "                  \"name\":\"pepperoni\"," + "                  \"price\":134.55,"
            + "                  \"tax\":100," + "                  \"vat\":134.55" + "               }"
            + "            }" + "         ]," + "         \"articles\":{  " + "            \"id\":12,"
            + "            \"name\":\"pica\"," + "            \"description\":\"pica desc\","
            + "            \"price\":3" + "         }" + "      }," + "      {  " + "         \"id\":363,"
            + "         \"count\":1," + "         \"price\":32.34," + "         \"attributes\":[  " + "            {  "
            + "               \"id\":211," + "               \"price\":\"0.99\"," + "               \"attribute\":{  "
            + "                  \"id\":21," + "                  \"name\":\"Extra cheese\","
            + "                  \"price\":1," + "                  \"tax\":1," + "                  \"vat\":1"
            + "               }" + "            }" + "         ]," + "         \"articles\":{  "
            + "            \"id\":13," + "            \"name\":\"Hamburger\","
            + "            \"description\":\"Test 3 Article\"," + "            \"price\":33" + "         }" + "      },"
            + "      {  " + "         \"id\":364," + "         \"count\":2," + "         \"price\":32.34,"
            + "         \"articles\":{  " + "            \"id\":14," + "            \"name\":\"Coca Cola\","
            + "            \"description\":\"Test 3 Article\"," + "            \"price\":33" + "         }" + "      }"
            + "" + "   ]," + "   \"cashier\":{  " + "      \"id\":3," + "      \"name\":\"asllan\","
            + "      \"last_name\":\"smith\"," + "      \"email\":\"dsxsassd@gmail.com\","
            + "      \"username\":\"asllan.smith2\"," + "      \"gender\":\"Male\","
            + "      \"date_of_birth\":\"2019-01-05\"," + "      \"address\":\"Prishtine\","
            + "      \"house_nr\":\"22\"," + "      \"phone_number\":\"044123565\"," + "      \"role\":\"Operator\","
            + "      \"role_id\":{  " + "         \"id\":3" + "      }," + "      \"city_id\":2,"
            + "      \"company_id\":2" + "   }," + "   \"user\":{  " + "      \"id\":14," + "      \"name\":\"Will\","
            + "      \"last_name\":\"Smith\"," + "      \"email\":\"aadwdawdi@gmail.com\","
            + "      \"username\":\"ciki\"," + "      \"gender\":\"male\"," + "      \"date_of_birth\":\"2011-07-05\","
            + "      \"address\":\"Kosovo\"," + "      \"house_nr\":\"23\"," + "      \"phone_number\":\"0445655667\","
            + "      \"role\":\"Customer\"," + "      \"role_id\":{  " + "         \"id\":4" + "      },"
            + "      \"city_id\":5," + "      \"company_id\":2" + "   }" + "}]";

    private AidlDeviceService mAidlDeviceService = null; // deklarimi i paisje ne baze te API
    private AidlPrinter mPrinter = null; // deklarimi i printerit

    @Override
    protected void pluginInitialize() {
        connectBinderPoolService();
    }

    /**
     * Inicializimi i Paisjes
     */
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mAidlDeviceService = AidlDeviceService.Stub.asInterface(service);
            try {
                // metoda per inicializimin e paisjes apo mappimi deri te pathi per me perdor
                // paisjen
                mPrinter = AidlPrinter.Stub.asInterface(mAidlDeviceService.getPrinter()); // inicializimi i printerit
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("PrinterPlugin", "===onServiceDisconnected===");
        }
    };

    private synchronized void connectBinderPoolService() {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.huiyi.ypos.usdk", "com.huiyi.ypos.usdk.MyService"));
        this.cordova.getActivity().getApplicationContext().bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }

    public void print(JSONArray args, CallbackContext callbackContext) {
        if (mAidlDeviceService != null) {
            try {
                mPrinter = AidlPrinter.Stub.asInterface(mAidlDeviceService.getPrinter()); // inicializimi i paisjes
                if (mPrinter != null) {
                    try {
                        String jsonStr = "[";
                        JSONObject json = new JSONArray(data).getJSONObject(0);
                        JSONObject company = json.getJSONObject("company");
                        String companyName = company.getString("company_name");
                        jsonStr += format("txt", 2, 1, "center", companyName, null, null, false);
                        String companyAddress = company.getString("address");
                        jsonStr += format("txt", 2, 1, "center", companyAddress, null, null, false);
                        String city = company.optString("city");
                        String country = company.optString("country");
                        String countrAndCity = "";
                        if (city != null && country != null) {
                            countrAndCity = city + ", " + country;
                            jsonStr += format("txt", 2, 1, "center", countrAndCity, null, null, false);
                        }
                        String phone = company.optString("phone");
                        if (phone != null)
                            jsonStr += format("txt", 2, 1, "center", "Tel: " + phone, null, null, false);
                        String web = company.optString("web");
                        if (web != null)
                            jsonStr += format("txt", 2, 1, "center", web, null, null, false);
                        String fiscalNum = company.getString("fiscal_num");
                        String number = json.getString("number");
                        jsonStr += format("txt", 2, 1, "center", "Fiscal Num: " + fiscalNum, null, null, false);
                        jsonStr += format("txt", 5, 5, "center", "=====================", null, null, false);
                        jsonStr += format("txt", 5, 1, "center", "INVOICE", null, null, false);
                        jsonStr += format("txt", 3, 1, "center", number, null, null, false);
                        jsonStr += format("txt", 3, 1, "center", "---------------", null, null, false);
                        JSONArray items = json.getJSONArray("items");
                        String itemStr = "";
                        for (int i = 0; i < items.length(); i++) {
                            JSONObject item = (JSONObject) items.get(i);
                            String article = item.getJSONObject("articles").getString("name").trim();
                            double price = item.getDouble("price");
                            int quantity = item.getInt("count");
                            int noOfSpaces = calculateSpace(article, price + "€", quantity + "x", 2);
                            // System.out.println("spaces: " + noOfSpaces + " for "+article);
                            String s = String.format("%d x %s", quantity, article);
                            for (int k = 0; k < noOfSpaces; k++)
                                s += " ";
                            s += price + "€";
                            jsonStr += format("txt", 2, 1, "left", s, null, null, false);
                            itemStr += s + "\n";
                            JSONArray attributes = item.optJSONArray("attributes");
                            if (attributes != null)
                                for (int j = 0; j < attributes.length(); j++) {
                                    JSONObject itemAttribute = (JSONObject) attributes.get(j);
                                    String itemAttributePrice = itemAttribute.getString("price");
                                    String itemAttributeName = itemAttribute.getJSONObject("attribute")
                                            .getString("name").trim();
                                    noOfSpaces = calculateSpace(itemAttributeName + "- ", itemAttributePrice + "€", "",
                                            1);
                                    s = String.format(" - %s", itemAttributeName);
                                    for (int k = 0; k < noOfSpaces; k++)
                                        s += " ";
                                    s += String.format("%s", itemAttributePrice + "€");
                                    jsonStr += format("txt", 2, 1, "left", s, null, 1, false);
                                    itemStr += s + "\n";
                                } // end of attributes
                        } // end of items
                        String dateString = new SimpleDateFormat("dd-MM-yyyy hh:mm aa", Locale.getDefault())
                                .format(new Date());
                        double price = json.getDouble("price");
                        double vat = json.getDouble("tax");
                        double priceWithVat = json.getDouble("vat");
                        String priceStr = String.format("Price: %.2f€", price);
                        String vatStr = String.format("VAT: %.2f€", vat);
                        String priceWithVatStr = String.format("TOTAL: %.2f€", priceWithVat);
                        String dateStr = String.format("Date: %s", dateString);
                        String cashier = json.getJSONObject("cashier").getString("name") + " "
                                + json.getJSONObject("cashier").getString("last_name");
                        jsonStr += format("txt", 5, 1, "right", "-----------", null, null, false);
                        jsonStr += format("txt", 2, 1, "right", priceStr, null, null, false);
                        jsonStr += format("txt", 2, 1, "right", vatStr, null, null, false);
                        jsonStr += format("txt", 2, 1, "right", priceWithVatStr, null, null, false);
                        jsonStr += format("txt", 5, 1, "center", "---------------------", null, null, false);
                        jsonStr += format("txt", 2, 1, "left", cashier, null, null, false);
                        jsonStr += format("txt", 2, 1, "left", dateStr, null, null, false);
                        jsonStr += format("two-dimension", 0, 0, "left", number, null, 0, false);
                        jsonStr += format("txt", 4, 1, "center", "Thank You!", null, null, false);
                        jsonStr += format("txt", 5, 5, "center", "=====================", null, null, false);
                        jsonStr += format("txt", 2, 1, "center", "\n\n", null, null, true);
                        jsonStr += "]";
                        List<Bitmap> bitmaps = new ArrayList<Bitmap>();
                        mPrinter.print(jsonStr, bitmaps, new AidlPrinterListener.Stub() {
                            @Override
                            public void onStart() throws RemoteException {
                                Log.d("PrinterPlugin", "onStart...");
                            }

                            @Override
                            public void onFinish() throws RemoteException {
                                Log.d("PrinterPlugin", "onFinish...");
                            }

                            @Override
                            public void onError(int errorCode, String detail) throws RemoteException {
                                Log.d("PrinterPlugin", "onError...");
                            }
                        });

                    } catch (RemoteException e) {
                        callbackContext.error("RemoteException is thrown." + e);
                        e.printStackTrace();
                    } catch (JSONException e) {
                        callbackContext.error("JSONException is thrown." + e);
                        e.printStackTrace();
                    }
                } else {
                    Log.d("PrinterPlugin", "AidlAppInstallListener is null");
                    callbackContext.error("mPrinter is null.");
                }
            } catch (RemoteException e) {
                callbackContext.error("RemoteException is thrown." + e);
                e.printStackTrace();
            }
        } else {
            callbackContext.error("mAidlDeviceService is null.");
        }
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        Log.d("PrinterPlugin", "===execute===");
        if (action.equals("add")) {
            this.add(args, callbackContext);
            return true;
        } else if (action.equals("substract")) {
            this.substract(args, callbackContext);
            return true;
        } else if (action.equals("print")) {
            Log.d("PrinterPlugin", "===print===");
            // callbackContext.success("Invoice printed");
            print(args, callbackContext);
            return true;
        }
        return false;
    }

    private void add(JSONArray args, CallbackContext callbackContext) {
        if (args != null) {
            try {
                int p1 = Integer.parseInt(args.getJSONObject(0).getString("param1"));
                int p2 = Integer.parseInt(args.getJSONObject(0).getString("param2"));
                Log.d("PrinterPlugin", "===args===" + p1 + ", " + p2);
                callbackContext.success(p1 + p2 + "");
            } catch (Exception e) {
                callbackContext.error("Something went wrong." + e);
            }
        } else
            callbackContext.error("Please do not pass null value.");
    }

    private void substract(JSONArray args, CallbackContext callbackContext) {
        if (args != null) {
            try {
                int p1 = Integer.parseInt(args.getJSONObject(0).getString("param1"));
                int p2 = Integer.parseInt(args.getJSONObject(0).getString("param2"));
                callbackContext.success(p1 - p2 + "");
            } catch (Exception e) {
                callbackContext.error("Something went wrong." + e);
            }
        } else
            callbackContext.error("Please do not pass null value.");
    }

    private static String format(String contentType, Integer size, Integer bold, String position, String content,
            Integer height, Integer offset, boolean last) {

        String result = String.format("{content-type:\"%s\",size:%d,bold:%d,position:\"%s\",content:\"%s\"",
                contentType, size, bold, position, content);
        if (offset != null)
            result += String.format(", offset:%d", offset);
        if (height != null)
            result += String.format(", height:%d", height);
        result += "}";
        if (!last)
            result += ",";
        result += "\n";
        return result;
    }

    private static int calculateSpace(String item, String price, String count, int extra) {
        int n = item.length();
        int m = price.length();
        return MAX - n - m - count.length() - extra;
    }

}
