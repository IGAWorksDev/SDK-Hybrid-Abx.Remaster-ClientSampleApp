//
//  ViewController.swift
//  HybridTest
//
//  Created by Jimmy.강세훈 on 2023/03/14.
//

import UIKit
import WebKit

class ViewController: UIViewController {

    var webView: WKWebView!
    
    let interfaceName = "adbrixBridge"
    let webViewURL = "https://web-sdk-dev-sec-dir.public.sre.dfinery.io/"
    
    override func loadView() {
        let webConfiguration = WKWebViewConfiguration()
        let contentController = WKUserContentController()
        contentController.add(WeakContentController(delegate: self), name: interfaceName)
        webConfiguration.userContentController = contentController
        
        webView = WKWebView(frame: .zero, configuration: webConfiguration)
        
        view = webView
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        webViewInit()
    }
    
    private func webViewInit() {
        if let url = URL(string: webViewURL) {
            let request = URLRequest(url: url)
            webView.load(request)
        }
        
        //WebView cache 삭제
        let dataTypes = NSSet(array: [WKWebsiteDataTypeDiskCache, WKWebsiteDataTypeMemoryCache])
        let date = Date(timeIntervalSince1970: 0)
        WKWebsiteDataStore.default().removeData(ofTypes: dataTypes as! Set<String>, modifiedSince: date, completionHandler: {})
    }
}

extension ViewController: WKScriptMessageHandler {
    
    func userContentController(_ userContentController: WKUserContentController, didReceive message: WKScriptMessage) {
        if message.name == interfaceName {
            AdbrixJavascriptInterface.event(message: message.body)
        }
    }
    
}

//Memory leak을 해결하기 위한 클래스
class WeakContentController: NSObject, WKScriptMessageHandler {
    weak var delegate: WKScriptMessageHandler?
    
    init(delegate: WKScriptMessageHandler) {
        self.delegate = delegate
        super.init()
    }
    
    func userContentController(_ userContentController: WKUserContentController, didReceive message: WKScriptMessage) {
        self.delegate?.userContentController(userContentController, didReceive: message)
    }
}
