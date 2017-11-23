cordova.define("cordova-plugin-zitopay.ZITOPay", function(require, exports, module) {
var exec = require('cordova/exec');

var zitopay = function(){};

zitopay.prototype.AliPay = function(arg,success,error) {
  exec(success, error, "ZITOPay", "ALiPay", [arg]);
};

zitopay.prototype.WeiXinPay = function(arg,success,error) {
  exec(success, error, "ZITOPay", "WXPay", [arg]);
};

var pay = new zitopay();
module.exports = pay;
});
