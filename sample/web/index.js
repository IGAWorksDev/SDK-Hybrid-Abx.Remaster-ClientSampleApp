// Method Name
const AdbrixMethodName = {
    login: "login",
    logout: "logout",
    signUp: "signUp",
    purchase: "purchase",
    viewHome: "viewHome"
}

// SignUp Channel
const AdbrixSignUpChannel = {
    AdBrixRmSignUpKakaoChannel: 1,
    AdBrixRmSignUpNaverChannel: 2,
    AdBrixRmSignUpLineChannel: 3,
    AdBrixRmSignUpGoogleChannel: 4,
    AdBrixRmSignUpFacebookChannel: 5,
    AdBrixRmSignUpTwitterChannel: 6,
    AdBrixRmSignUpWhatsAppChannel: 7,
    AdBrixRmSignUpQQChannel: 8,
    AdBrixRmSignUpWeChatChannel: 9,
    AdBrixRmSignUpUserIdChannel: 10,
    AdBrixRmSignUpETCChannel: 11,
    AdBrixRmSignUpSkTidChannel: 12,
    AdBrixRmSignUpAppleIdChannel: 13
};

// Payment Method
const AdbrixPaymentMethod = {
    CreditCard: 1,
    BankTransfer: 2,
    MobilePayment: 3,
    ETC: 4
}

// Gender
const AdbrixGenderType = {
    Male: 2,
    Female: 1,
    Unknown: 0
}

// Currency
const AdbrixCurrency = {
    KRW: 1,
    USD: 2,
    JPY: 3,
    EUR: 4,
    GBP: 5,
    CNY: 6,
    TWD: 7,
    HKD: 8,
    IDR: 9,
    INR: 10,
    RUB: 11,
    THB: 12,
    VND: 13,
    MYR: 14
}

// 접속한 기기가 안드로이드면 true를 반환
function isAndroidBridgeAvailable() {
    var result = false;
    if (window.adbrixBridge) {
        result = true;
    }
    if (!result) {
        console.log("No Android APIs found.");
    }
    return result;
}

// 접속
function isIosBridgeAvailable() {
    var result = false;
    if (window.webkit
        && window.webkit.messageHandlers
        && window.webkit.messageHandlers.adbrixBridge) {
        result = true;
    }
    if (!result) {
        console.log("No iOS APIs found.");
    }
    return result;
}

// Invite channel
const AdbrixInviteChannel = {
    AdBrixRmInviteKakaoChannel: 1,
    AdBrixRmInviteNaverChannel: 2,
    AdBrixRmInviteLineChannel: 3,
    AdBrixRmInviteGoogleChannel: 4,
    AdBrixRmInviteFacebookChannel: 5,
    AdBrixRmInviteTwitterChannel: 6,
    AdBrixRmInviteWhatsAppChannel: 7,
    AdBrixRmInviteQQChannel: 8,
    AdBrixRmInviteWeChatChannel: 9,
    AdBrixRmInviteETCChannel: 10
}

function login() {
    var userId = "login_id";
    const param = {
        method_name: AdbrixMethodName.login,  // 원하는 API명을 입력.
        user_id: userId
    };
    adbrix.login(userId);
    if (isAndroidBridgeAvailable()) {
        adbrixBridge.invoke(JSON.stringify(param));
    }
    if (isIosBridgeAvailable()) {
        window.webkit.messageHandlers.adbrixBridge.postMessage(param);
    }
}

function logout() {
    const param = {
        method_name: AdbrixMethodName.logout
    }
    adbrix.logout();
    if (isAndroidBridgeAvailable()) {
        adbrixBridge.invoke(JSON.stringify(param));
    }
    if (isIosBridgeAvailable()) {
        window.webkit.messageHandlers.adbrixBridge.postMessage(param);
      }
}

function signUp() {
    const param = {
        method_name: AdbrixMethodName.signUp,
        sign_channel: AdbrixSignUpChannel.Google,  // number
        extra_attr: {
            user_id: "user_id",
            age: 14  // number
        }
    }
    adbrix.common.signUp('Google', {
        user_id: "user_id",
        age: 14  // number
     });
    if (isAndroidBridgeAvailable()) {
        adbrixBridge.invoke(JSON.stringify(param));
    }
    if (isIosBridgeAvailable()) {
        window.webkit.messageHandlers.adbrixBridge.postMessage(param);
    }
}

function purchase() {
    var orderId = "order_id";
    var orderSales = 10.0;
    var productId = "product_id";
    var productName = "product_name";
    var price = 10.0;
    var quantity = 1;
    var discount = 1200.00;
    var deliveryCharge = 0;
    var payment = AdbrixPaymentMethod.BankTransfer;
    var categories = { category1: "category1", category2: "category2", category3: "category3", category4: "category4" };

    const products = [];
    products.push({
        product_id: productId,
        product_name: productName,
        price: price,
        quantity: quantity,
        discount: discount,
        currency: AdbrixCurrency.CNY,
        categories: categories
    });
    products.push({
        product_id: productId,
        product_name: productName,
        price: price,
        quantity: quantity,
        discount: discount,
        currency: AdbrixCurrency.CNY,
        categories: categories
    });

    const param = {
        method_name: AdbrixMethodName.purchase,
        order_id: "orderID",
        order_sales: orderSales,
        discount: discount,
        delivery_charge: deliveryCharge,
        payment_method: AdbrixPaymentMethod.BankTransfer,
        quantity: 1,
        items: products,
        extra_attr: {
            attrKey: "attrValue"
        }
    }
    adbrix.common.purchase(
        orderId,                      // 주문번호 order_id
        products,                     // 상품리스트 product[]
        orderSales,                      // 전체 주문 금액 order sales
        discount,                      // 할인 금액 discount
        1000,                      // 배송비 delivery charge
        payment,                        // 결제 방법 payment
        null              // 결제 프로퍼티 properties
    );
    if (isAndroidBridgeAvailable()) {
        adbrixBridge.invoke(JSON.stringify(param));
    }
    if (isIosBridgeAvailable()) {
        window.webkit.messageHandlers.adbrixBridge.postMessage(param);
    }
}

function viewHome() {
    const param = {
        method_name: AdbrixMethodName.viewHome
    }
    adbrix.viewHome();
    if (isAndroidBridgeAvailable()) {
        adbrixBridge.invoke(JSON.stringify(param));
    }
    if (isIosBridgeAvailable()) {
        window.webkit.messageHandlers.adbrixBridge.postMessage(param);
    }
}
