## Adbrix SDK Bridge 연동하기

이 가이드는 웹뷰를 사용하는 어플리케이션에서 Bridge를 사용하여 SDK를 호출하는 내용을 설명합니다. 

### Step 1. Application 연동하기

SDK는 init시 Application을 필요로 합니다. `Application#onCreate()`에서 다음과 같이 SDK를 초기화 해주시기 바랍니다. 

없을 경우 Application을 상속 받는 클래스를 생성해야 하며 사용하고 계시는 클래스가 있다면 해당 Application에 연동해야 합니다.

> SDK의 기본적인 연동은 [헬프센터](https://help.dfinery.io/hc/ko/articles/360003279994-애드브릭스-연동하기-Android-)를 참고하여 주시기 바랍니다.

```java
public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AdBrixRm.init(this, "{앱키}", "{시크릿키}");
        ...
    }
}
```


### Step 2. 사용하고 계시는 웹뷰에 Bridge를 등록해주시기 바랍니다.

Bridge는 웹페이지와 SDK를 통신하기 위해 [예시용 JavascriptInterface](file/AdbrixJavascriptInterface.java)가 작성되어 있으며, 웹페이지에서 호출한 `invoke`  Javascript function을 통해 데이터가 수신되어 동작하게끔 되어 있습니다.

> 웹페이지와 동일한 Bridge 명칭을 써야 합니다. 데모 프로젝트에는 `adbrixBridge`라는 명칭으로 연동되어 있습니다.

```java
webView.addJavascriptInterface(new AdbrixJavascriptInterface(getApplicationContext()), "adbrixBridge");
```

### Step 3. 웹페이지에서 Bridge가 연동되어 있는지 확인합니다.

다음 스크립트 function을 사용하면 해당 시점에 Bridge가 연동되어 있는지 확인할수 있습니다.

```javascript
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
```

### Step 4. 웹페이지에서 Bridge를 활용하여 Native 이벤트를 호출합니다.

```javascript
//로그인 호출 예시
function login(userId){
    const param = {
        method_name : AdbrixMethodName.login,  // 원하는 API명을 입력.
        user_id : userId
    };

    if (isAndroidBridgeAvailable()) {
        adbrixBridge.invoke(param);
    } 
}
```