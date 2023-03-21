//
//  ViewController.m
//  HybridTestObjc
//
//  Created by Jimmy.강세훈 on 2023/03/21.
//

#import "ViewController.h"
#import "AdBrixJavascriptInterface.h"

@interface ViewController ()

@end

@implementation ViewController

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
    
    NSSet* nSet= [NSSet setWithArray:@[WKWebsiteDataTypeDiskCache, WKWebsiteDataTypeMemoryCache, WKWebsiteDataTypeCookies]];
    NSDate *nDate=[NSDate dateWithTimeIntervalSince1970:0];
    [WKWebsiteDataStore.defaultDataStore removeDataOfTypes:nSet modifiedSince:nDate completionHandler:^{}];

}

#pragma mark - WKScriptMessageHandler

- (void)userContentController:(WKUserContentController *)userContentController didReceiveScriptMessage:(WKScriptMessage *)message {
    if ([message.name isEqualToString:@"adbrixBridge"]) {
        [AdBrixJavascriptInterface event:message.body];
    }
}

@end
