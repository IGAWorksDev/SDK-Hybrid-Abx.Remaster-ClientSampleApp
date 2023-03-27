## Adbrix SDK Bridge 연동하기

이 가이드는 웹뷰를 사용하는 어플리케이션에서 Bridge를 사용하여 SDK를 호출하는 내용을 설명합니다. 

### Step 1. SDK 초기화 하기

SDK를 사용하기 위해선 AppDelegate에서의 initAdbrix 메소드 호출이 필요합니다.

> SDK의 기본적인 연동은 [헬프센터](https://help.dfinery.io/hc/ko/articles/900005450503-디파이너리-애드브릭스-연동하기-iOS-)를 참고하여 주시기 바랍니다.

```objective-c
#import "AppDelegate.h"
#import <AdBrixRmKit/AdBrixRmKit.h>

@interface AppDelegate ()

@end

@implementation AppDelegate


- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    // Override point for customization after application launch.
    
    AdBrixRM *adBrix = [AdBrixRM sharedInstance];
    [adBrix initAdBrixWithAppKey:@"앱키" secretKey:@"시크릿키"];
    return YES;
}
```

### Step 2. WKWebView에 적용할 userContentController를 ViewController에 작성합니다. 

```objective-c
- (void)userContentController:(WKUserContentController *)userContentController didReceiveScriptMessage:(WKScriptMessage *)message {
    if ([message.name isEqualToString:@"adbrixBridge"]) {
       ...
    }
}

```

### Step 3.  userContentController에 Bridge 연동 코드를 작성합니다.

Bridge는 웹페이지와 SDK를 통신하기 위해 [예시용 JavascriptInterface](file/AdBrixJavascriptInterface.m)가 작성되어 있으며, 웹페이지에서 호출한 `postMessage`  Javascript function을 통해 데이터가 수신되어 동작하게끔 되어 있습니다.

> 웹페이지와 동일한 Bridge 명칭을 써야 합니다. 데모 프로젝트에는 `adbrixBridge`라는 명칭으로 연동되어 있습니다.

```objective-c
- (void)userContentController:(WKUserContentController *)userContentController didReceiveScriptMessage:(WKScriptMessage *)message {
    if ([message.name isEqualToString:@"adbrixBridge"]) {
        [AdBrixJavascriptInterface event:message.body];
    }
}
```

### Step 4. ViewController 헤더파일에 Webkit을 import하고 WKWebView 프로퍼티를 선언합니다.

```objective-c
#import <UIKit/UIKit.h>
#import <Webkit/WebKit.h>

@interface ViewController : UIViewController <WKScriptMessageHandler>

@property (strong, nonatomic) WKWebView *webView;

@end
```

### Step 5. ViewController의 viewDidLoad 메서드에서 웹뷰를 생성하고 userContentController(Bridge)를 등록하여 주시기 바랍니다.

```objective-c
- (void)viewDidLoad {
    [super viewDidLoad];
    
    WKWebViewConfiguration *config = [[WKWebViewConfiguration alloc] init];
    WKUserContentController *contentController = [[WKUserContentController alloc] init];
    [contentController addScriptMessageHandler:self name:@"adbrixBridge"];
    config.userContentController = contentController;

    self.webView = [[WKWebView alloc] initWithFrame:self.view.bounds configuration:config];
    [self.view addSubview:self.webView];

    NSURL *url = [NSURL URLWithString:@"https://web-sdk-dev-sec-dir.public.sre.dfinery.io/"];
    NSURLRequest *request = [NSURLRequest requestWithURL:url];
    [self.webView loadRequest:request];
    
    //웹뷰의 캐시를 삭제하는 코드입니다.
    NSSet* nSet= [NSSet setWithArray:@[WKWebsiteDataTypeDiskCache, WKWebsiteDataTypeMemoryCache, WKWebsiteDataTypeCookies]];
    NSDate *nDate=[NSDate dateWithTimeIntervalSince1970:0];
    [WKWebsiteDataStore.defaultDataStore removeDataOfTypes:nSet modifiedSince:nDate completionHandler:^{}];

}
```

### Step 6. 웹페이지에서 Bridge가 연동되어 있는지 확인합니다.

다음 스크립트 function을 사용하면 해당 시점에 Bridge가 연동되어 있는지 확인할수 있습니다.

```javascript
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
```

### Step 7. 웹페이지에서 Bridge를 활용하여 Native 이벤트를 호출합니다.

```javascript
//로그인 호출 예시
function login(userId){
    const param = {
        method_name : AdbrixMethodName.login,  // 원하는 API명을 입력.
        user_id : userId
    };

    if (isIosBridgeAvailable()) {
        window.webkit.messageHandlers.adbrixBridge.postMessage(param);
    }
}
```

### Step 8. 완료 되었습니다.

---

### Bridge 작성 요령

#### 웹페이지에서 Native에 전달되는 이벤트는 전부 `postMessage` function을 통해 들어옵니다. 

```javascript
const param = {
    method_name : AdbrixMethodName.login,  // 원하는 API명을 입력.
    user_id : userId
};
	
if (isIosBridgeAvailable()) {
	window.webkit.messageHandlers.adbrixBridge.postMessage(param);
}
```

- SDK의 동작해야할 메소드는 `method_name` 키 값으로 설정하여 AdbrixJavascriptInterface에 전달해야합니다.

```objective-c
- (void)userContentController:(WKUserContentController *)userContentController didReceiveScriptMessage:(WKScriptMessage *)message {
    if ([message.name isEqualToString:@"adbrixBridge"]) {
        [AdBrixJavascriptInterface event:message.body];
    }
}
```
	
- 해당하는 메소드에 필요한 파라미터를 데이터에 넣고 전달하여 SDK 메소드를 호출해야 합니다.

```objective-c
+ (void)actionHandler:(NSDictionary *)json {
    AdBrixRM *adBrix = [AdBrixRM sharedInstance];
    NSString *method = json[@"method_name"];
    
    if ([method isEqualToString:@"login"]) {
        NSString *userId = json[@"user_id"];
        
        [adBrix loginWithUserId:userId];
    }
    else if ([method isEqualToString:@"logout"]) {
        [adBrix logout];
    }
    else if ...
```
