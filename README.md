つぶのみ
======================
AndroidのTwitterクライアントアプリケーションです。  
ホームタイムラインとリストを見ないで作業するために作りました。  
ツイートとダイレクトメッセージの送信機能と、ユーザータイムラインの参照と検索機能だけです。  
添付写真はTwitterの公式写真投稿サービスへ投稿されます。  
ツイート本文とダイレクトメッセージの140文字カウントは、Twitter公式の短縮サービスt.coに対応して表示しています。公式の写真投稿分もカウントされます。  
メールアプリのメール本文などのreply,DMへのリンクから、暗黙的インテントを受け取り、返信することが可能です。  
他アプリからのテキスト、画像のインテント受け取りも対応しています。  
DM画面は、暗黙インテントからのみアクセスします。  
Android 2.3.3 以上  
Galaxy S2,Android 2.3.3のみで検証しています。  

動かしてみる場合
----------
src/jp/katahirado/android/tsubunomi/Const.javaの CONSUMER_KEY と CONSUMER_SECRET を埋めてご使用ください。  


使用ライブラリ
----------
[Apache License, Version 2.0][Apache]に基づいてリリースされている[Twitter4J][4j],及び[twitter-text-java][ttj]を使用しております。  

ライセンス
----------
Copyright &copy; 2012 katahirado  
Distributed under the [MIT License][mit].  

[Apache]: http://www.apache.org/licenses/LICENSE-2.0]
[MIT]: http://www.opensource.org/licenses/mit-license.php
[ttj]: https://github.com/twitter/twitter-text-java
[4j]: http://twitter4j.org/ja/index.html