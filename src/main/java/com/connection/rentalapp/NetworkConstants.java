package com.connection.rentalapp;

/**
 * Created by Sainath on 14-08-2015.
 */
public class NetworkConstants {
    public static final boolean isServerON = true;
    private static final String HOST_ADDRESS = "192.168.0.151";
    private static final String SERVER_ADDRESS = "ec2-52-88-192-50.us-west-2.compute.amazonaws.com";
    private static final String HOST_URL = "http://"+SERVER_ADDRESS+":8080/C2CReach/";

    public static final String SAVE_ITEM = HOST_URL + "item/saveItem";
    /* POST Request
      {"id":567,"category":"Photography","subcategory":"Camera","name":"Sony","description":"Camera with good condition",
       "age":12.34,"duration":3.0,"price":12.34,"userName":"testUser","startTime":1438277325000,"endTime":1438277325000,
       "distance":null,"insured":null,"thumbnailList":null,"binaryList":null}*/
    public static final String GET_ITEMS = HOST_URL + "item/items";
    /* POST & GET Request
      {"searchList":[{"name":"CATEGORY","value":"Photo"},{"name":"SUBCATEGORY","value":"Camera"}]}*/
    public static final String GET_ITEM = HOST_URL + "item/getItem/"; //Item id parameter should append
    /* GET Request
       http://localhost:8080/C2CReach/item/getItem/123*/
    public static final String RESERVE_ITEM = HOST_URL + "item/reserveItem";
    /* POST Request
      {"userName":"testUser","itemId":457,"startTime":1438630554702,"endTime":1438630554702,"reservedTime":1438630554702,
       "price":23.45}*/

    public static final String SAVE_USER = HOST_URL + "user/saveUser";
    /* POST Request
      {"userName":"lpr2","password":"test1","firstName":"Test","lastName":"Test","initials":"Mr.",
       "birthDate":1436295682000,"gender":"M","emailId":"test@test.com","mobileNumber":"0031644156903",
       "officeNumber":"0031644156903"}*/
    public static final String GET_USER = HOST_URL + "user/getUser?userId="; //username parameter should append
    /* GET Request
       http://192.168.0.151:8080/C2CReach/user/getUser?userId=lpr2*/

    public static final String SAVE_USER_ADDRESS = HOST_URL + "user/saveAddress";
    /* POST Request
      {"userName":"lpr2","houseNumber":"102","addressLine1":"Spurgeonlaan","addressLine2":"1185BE",
       "addressLine3":"Amstelveen","addressLine4":"North Holland","addressLine5":"Netherlands"}*/

    public static final String GET_USER_ADDRESS = HOST_URL + "user/getAddress?userId=";
    /*GET Request
    http://localhost:8080/C2CReach/user/getAddress?userId=lpr2
    {   userName: "lpr2" houseNumber: "102" addressLine1: "Spurgeonlaan" addressLine2: "1185BE" addressLine3: "Amstelveen"
        addressLine4: "North Holland" addressLine5: "Netherlands" longitude: 5 latitude: 52 }*/

    /*public static final String SAVE_ITEM = "http://192.168.2.3:8080/C2CReach/item/saveItem";
    public static final String GET_ITEMS = "http://192.168.2.3:8080/C2CReach/item/items";
    public static final String GET_ITEM = "http://192.168.2.3:8080/C2CReach/item/getItem/"; //Item id parameter should append

    public static final String SAVE_USER = "http://192.168.2.3:8080/C2CReach/user/saveUser";
    public static final String GET_USER = "http://192.168.2.3:8080/C2CReach/user/getUser/"; //username parameter should append

    public static final String SAVE_USER_ADDRESS = "http://192.168.2.3:8080/C2CReach/user/saveAddress";*/
}
