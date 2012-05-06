つぶのみ
======================
AndroidのTwitterクライアントアプリケーションです。  
つぶのみは送信専用です。  
ツイートとダイレクトメッセージの送信機能のみで、タイムライン等の参照機能はありません。  
添付写真はTwitterの公式写真投稿サービスへ投稿されます。  
ツイート本文の140文字カウントは、Twitter公式の短縮サービスt.coに対応しています。  
メール本文などのreply,DMへのリンクから、暗黙的インテントを受け取り、返信することが可能です。  
Galaxy S2,Android 2.3.3のみで検証しています。  

動かしてみる場合
----------
src/jp/katahirado/android/tsubunomi/Const.javaの CONSUMER_KEY と CONSUMER_SECRET を埋めてご使用ください。  


使用ライブラリ
----------
[Apache License, Version 2.0][Apache]に基づいてリリースされているTwitter4J,及びtwitter-text-javaを使用しております。  

ライセンス
----------
Copyright &copy; 2012 katahirado  
Distributed under the [MIT License][mit].  

[Apache]: http://www.apache.org/licenses/LICENSE-2.0]
[MIT]: http://www.opensource.org/licenses/mit-license.php
