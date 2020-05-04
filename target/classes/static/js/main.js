$(function () {
    $.ajax({
        type: "post",
        url: "/user/getAccept",
        headers: {
            'Authorization': window.sessionStorage.getItem("Authorization")
        },
        dataType: "json",
        success: function (result) {
            if(result.code==200) {
                if (result.msg == "1") {
                    $('#accept').prop("checked", true);
                    $('#accept_l').text("开启");
                } else {
                    $('#accept').prop("checked", false);
                    $('#accept_l').text("关闭");
                }
            }else alert(result.msg);
        },
    });
    $.ajax({
        type: "post",
        url: "/user/getHead",
        headers: {
            'Authorization': window.sessionStorage.getItem("Authorization")
        },
        dataType: "json",
        success: function (result) {
            if(result.code==200) {
                $('#head').attr("src","/user/download/"+result.msg+"?tempid="+Math.random());
            }else alert(result.msg);
        },
    });
    $('#head').click(function () {
        $('#setHead').click();
    });
    $('#ask').click(function () {
       var method = xssFilter($('#method').val());
       var answer = xssFilter($('#answer').val());
       var question = xssFilter($('#content').val());
       if(answer==""||question==""){
           alert("被提问人和问题内容不能有空!");
           return false;
       }
       if(method==1){
           $.ajax({
               type: "post",
               url: "/question/ask/username",
               headers: {
                   'Authorization': window.sessionStorage.getItem("Authorization")
               },
               data: {username: answer, content: question},
               dataType: "json",
               success: function (result) {
                   if(result.code==200) {
                       alert(result.msg);
                   }else alert(result.msg);
               },
           })
       }else{
               $.ajax({
                   type: "post",
                   url: "/question/ask/email",
                   headers: {
                       'Authorization': window.sessionStorage.getItem("Authorization")
                   },
                   data: {email: answer, content: question},
                   dataType: "json",
                   success: function (result) {
                       if(result.code==200) {
                           alert(result.msg);
                       }else alert(result.msg);
                   },
               })
       }
    });
        $('#accept').change(function () {
            $.ajax({
                type: "post",
                url: "/user/changeAccept",
                headers: {
                    'Authorization': window.sessionStorage.getItem("Authorization")
                },
                success: function (result) {
                    if(result.code==200) {
                        if (result.msg == "1") {
                            $('#accept').prop("checked", true);
                            $('#accept_l').text("开启");
                        } else {
                            $('#accept').prop("checked", false);
                            $('#accept_l').text("关闭");
                        }
                    }else {
                        alert(result.msg);
                        $('#accept').prop("checked",!$('#accept').prop("checked"));
                    }
                },
            })
        });
    $('#activate').click(function () {
        $.ajax({
            type: "post",
            url: "/user/activate",
            headers: {
                'Authorization': window.sessionStorage.getItem("Authorization")
            },
            success: function (result) {
                alert(result.msg);
            },
            error: function () {
                alert("激活邮件发送失败!");
            }
        })
    });
    $('#manage').click(function () {
        $("#hidden").val(window.sessionStorage.getItem("Authorization"));
        $("#main").submit();
    });
    var num = 1;
    var total = 1;
    $.ajax({
        type: 'post',
        url: '/question/getAnswered',
        data: {pageNum: num},
        headers: {
            'Authorization': window.sessionStorage.getItem("Authorization")
        },
        dataType: 'json',
        success: function (result) {
            var answered = $('#questions');
            answered.html("");
            $.each(result.records,function (index,q) {
                var i = (num-1)*3+index+1;
                answered.append("<tr><th scope=\"row\">"+i+"</th>"+"<td>"+q.aid+"</td>"+"<td>"+q.content+"</td>"+"<td>"+q.answer+"</td></tr>")
            });
            total = result.pages;
        }
    })
    $('#previous').click(function (e) {
        e.preventDefault();
        if(num==1)return false;
        $.ajax({
            type: 'post',
            url: '/question/getAnswered',
            data: {pageNum: num-1},
            headers: {
                'Authorization': window.sessionStorage.getItem("Authorization")
            },
            dataType: 'json',
            success: function (result) {
                var answered = $('#questions');
                answered.html("");
                $.each(result.records,function (index,q) {
                    var i = (num-2)*3+index+1;
                    answered.append("<tr><th scope=\"row\">"+i+"</th>"+"<td>"+q.aid+"</td>"+"<td>"+q.content+"</td>"+"<td>"+q.answer+"</td></tr>")
                });
                num = result.current;
            }
        })
    })
    $('#next').click(function (e) {
        e.preventDefault();
        if(num==total)return false;
        $.ajax({
            type: 'post',
            url: '/question/getAnswered',
            data: {pageNum: num+1},
            headers: {
                'Authorization': window.sessionStorage.getItem("Authorization")
            },
            dataType: 'json',
            success: function (result) {
                var answered = $('#questions');
                answered.html("");
                $.each(result.records,function (index,q) {
                    var i = num*3+index+1;
                    answered.append("<tr><th scope=\"row\">"+i+"</th>"+"<td>"+q.aid+"</td>"+"<td>"+q.content+"</td>"+"<td>"+q.answer+"</td></tr>")
                });
                num = result.current;
            }
        })
    })
});
function xssFilter(str) {
    return str
        .replace(/&/g, '&amp;')
        .replace(/ /g, '&nbsp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#39;')
        .replace(/\r{0,}\n/g, '<br/>')
}