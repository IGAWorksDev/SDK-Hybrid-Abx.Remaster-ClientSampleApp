//
//  ViewController.h
//  HybridTestObjc
//
//  Created by Jimmy.강세훈 on 2023/03/21.
//

#import <UIKit/UIKit.h>
#import <Webkit/WebKit.h>

@interface ViewController : UIViewController <WKScriptMessageHandler>

@property (strong, nonatomic) WKWebView *webView;

@end

