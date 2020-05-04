$(function(){
    
    /**生成一个随机数**/
    function randomNum(min, max) {
        return Math.floor(Math.random() * (max - min) + min);
    }
    /**生成一个随机色**/
    function randomColor(min, max) {
        var r = randomNum(min, max);
        var g = randomNum(min, max);
        var b= randomNum(min, max);
        return "rgb(" + r + "," + g + "," + b + ")";
    }
    var code=drawPic();
    var canvas = $("#canvas");
    $("#changeImg").click (function(e) { 
        e.preventDefault();
        code=drawPic();
    });
    $("#changeImg3").click (function(e) { 
        e.preventDefault();
        code=drawPic();
    });
    $("#login-tab").click(function(){
        $("#changeImg").html("");
        $("#changeImg").append(canvas);
    });
    $("#register-tab").click(function(){
        $("#changeImg3").html("");
        $("#changeImg3").append(canvas);
    });

    /**绘制验证码图片**/
    function drawPic() {
        var canvas = document.getElementById("canvas");
        var width = canvas.width;
        var height = canvas.height;
        //获取该canvas的2D绘图环境 
        var ctx = canvas.getContext('2d'); 
        ctx.textBaseline ='bottom';
        /**绘制背景色**/
        ctx.fillStyle = randomColor(180,240);
        //颜色若太深可能导致看不清
        ctx.fillRect(0,0,width,height);
        /**绘制文字**/
        var str ='ABCEFGHJKLMNPQRSTWXY123456789';
　　　　 var code="";
　　　　　//生成四个验证码
        for(var i=1;i<=4;i++) {
            var txt = str[randomNum(0,str.length)];
            code=code+txt;
            ctx.fillStyle = randomColor(50,160);
            //随机生成字体颜色
            ctx.font = randomNum(15,40) +'px SimHei';
            //随机生成字体大小
            var x =10 +i *25;
            var y = randomNum(25,35);
            var deg = randomNum(-45,45);
            //修改坐标原点和旋转角度
            ctx.translate(x, y); 
            ctx.rotate(deg * Math.PI /180); 
            ctx.fillText(txt,0,0);
            //恢复坐标原点和旋转角度
            ctx.rotate(-deg * Math.PI /180);
            ctx.translate(-x, -y);
        }
        
        /**绘制干扰线**/
        for(var i=0;i<3;i++) {
            ctx.strokeStyle = randomColor(40, 180);
            ctx.beginPath();
            ctx.moveTo(randomNum(0,width/2), randomNum(0,height/2));
            ctx.lineTo(randomNum(0,width/2), randomNum(0,height));
            ctx.stroke();
        }
        /**绘制干扰点**/
        for(var i=0;i <50;i++) {
            ctx.fillStyle = randomColor(255);
            ctx.beginPath();
            ctx.arc(randomNum(0, width), randomNum(0, height),1,0,2* Math.PI);
            ctx.fill();
        }
        return code;
    }
    $('#login').click(function () {
        var $key = $('input[name="key"]').val();
        var $userName = $('#username').val();
        var $password = $('#password1').val();
        var $verifyCode = $('#code').val();
//            console.log($userName + ",  " + $password + ", " + $verifyCode + ", " + $key);
        if ($userName == "" || $password == "" || $verifyCode == "") {
            alert("用户名或邮箱、密码、验证码不能为空！");
            return false;
        }
        if($verifyCode.toLowerCase()===code.toLowerCase){
            alert("验证码错误!请重新输入");
            $('#code').val("");
            code=drawPic();//重新刷新验证码
            return false;
        }
        var key = CryptoJS.enc.Utf8.parse($key);
        var password = CryptoJS.enc.Utf8.parse($password);
        var encrypted = CryptoJS.AES.encrypt(password, key, {mode: CryptoJS.mode.ECB, padding: CryptoJS.pad.Pkcs7});
        var encryptedPwd = encrypted.toString();
//            console.log("encrypted:" + encrypted);
//            console.log("encryptedPwd:" + encryptedPwd);

        // var decrypt = CryptoJS.AES.decrypt(encryptedPwd, key, {
        //     mode: CryptoJS.mode.ECB,
        //     padding: CryptoJS.pad.Pkcs7
        // });
        // var testDecryptStr = CryptoJS.enc.Utf8.stringify(decrypt).toString();

//            console.log("decrypt:" + decrypt);
//            console.log("testDecryptStr:" + testDecryptStr);
        var method = $('#method').val();
        if(method==1){
        $.ajax({
                type: "post",
                url: "/user/login/username",
                data: {username: $userName, password: encryptedPwd,key: $key},
                dataType: "json",
                success: function (data,status,jqXHR) {
//                    console.log(result);
                    if(data.code==200)
                    {
                        window.sessionStorage.setItem("Authorization",jqXHR.getResponseHeader("Authorization"));
                        $("#hidden").val(jqXHR.getResponseHeader("Authorization"));
                        $("#main").submit();
                    } else
                    {
                        // $('h4[name="msg"]').html(result.msg);
                        alert(data.msg);
                        $('#code').val("");
                        code=drawPic();//重新刷新验证码
                    }
//                    window.location.replace(result.url);
//                    $('#container').load(result.url);
                },
                error: function (result) {
//                        console.log(result);
                    alert(result.msg);
                },

            });
        }else {
            $.ajax({
                type: "post",
                url: "/user/login/email",
                data: {email: $userName, password: encryptedPwd,key: $key},

                dataType: "json",
                success: function (result,status,jqXHR) {
//                    console.log(result);
                    if(result.code==200)
                    {
                        window.sessionStorage.setItem("Authorization",jqXHR.getResponseHeader("Authorization"));
                        $("#hidden").val(jqXHR.getResponseHeader("Authorization"));
                        $("#main").submit();
                    }
                    else
                    {
                        alert(result.msg);
                        $('#code').val("");
                        code=drawPic();//重新刷新验证码
                    }
//                    window.location.replace(result.url);
//                    $('#container').load(result.url);
                },
                error: function (result) {
//                        console.log(result);
                    alert(result.msg);
                }
            });
        }
    });
    $('#button2').click(function () {
        var $key = $('input[name="key"]').val();
        var $userName = $('#username2').val();
        var $email = $('#email_r').val();
        var $password = $('#password2').val();
        var $password_r = $('#password3').val();
        var $verifyCode = $('#code2').val();
        if($userName==""||$password==""||$password_r==""||$verifyCode==""||$email==""){
            alert("用户名、邮箱、密码和确认密码、验证码不能有空！");
            return false;
        }
        if($verifyCode.toLowerCase()!==code.toLowerCase()){
            alert("验证码错误!请重新输入");
            $('#code2').val("");
            code=drawPic();//重新刷新验证码
            return false;
        }
        if($password!==$password_r){
            alert("两次密码不一致,请重新输入");
            return false;
        }
        var key = CryptoJS.enc.Utf8.parse($key);
        var password = CryptoJS.enc.Utf8.parse($password);
        var encrypted = CryptoJS.AES.encrypt(password, key, {mode: CryptoJS.mode.ECB, padding: CryptoJS.pad.Pkcs7});
        var encryptedPwd = encrypted.toString();
        $.ajax({
            type: "post",
            url: "/user/register",
            data: {username: $userName,email: $email,password: encryptedPwd,key: $key},
            dataType: "json",
            success: function (result) {
                if(result.code==200)
                {
                    alert("注册成功！");
                    window.location.reload();
                }
                else
                {
                    alert(result.msg);
                    $('#code2').val("");
                    code=drawPic();//重新刷新验证码
                }
            },
            error: function (result) {
                alert("传递的参数错误!");
            }
        })
    });
    $('#button3').click(function () {
        var $email = $('#email3').val();
        $.ajax({
            type: "post",
            url: "/forget",
            data: {email: $email},
            dataType: "json",
            success: function (result) {
//                    console.log(result);
                if(result.code==200)
                {
                    alert("重置链接发送成功!有效时间:30分钟");
                    window.location.reload();
                }
                else
                {
                    alert(result.msg);
                }
//                    window.location.replace(result.url);
//                    $('#container').load(result.url);
            },
            error: function (result) {
//                        console.log(result);
                alert(result.msg);
            }
        });
    })
});