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
        contentController.add(self, name: interfaceName)
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
    }
}

extension ViewController: WKScriptMessageHandler {
    
    func userContentController(_ userContentController: WKUserContentController, didReceive message: WKScriptMessage) {
        
        guard message.name == interfaceName,
              let json = message.body as? [String: Any],
              let action = json["method_name"] as? String else {return}
        
        guard let event = AdbrixJavascriptInterface(rawValue: action) else {return}
        
        event.invoke(json: json)
    }
    
}

