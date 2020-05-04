var cnum = 1;
var ctotal = 1;
var unum = 1;
var utotal = 1;
var snum = 1;
var stotal = 1;
var qnum = 1;
var qtotal = 1;
var uid;
$(function () {
    $.ajax({
        type: "post",
        url: "/user/getHead",
        headers: {
            'Authorization': window.sessionStorage.getItem("Authorization")
        },
        dataType: "json",
        success: function (result) {
            if(result.code==200) {
                $('#head').attr("src","/user/download/"+result.msg);
            }else alert(result.msg);
        },
    });
    c(cnum);
    $('#cprevious').click(function (e) {
        e.preventDefault();
        if (cnum == 1) return false;
        c(cnum-1);
    });
    $('#cnext').click(function (e) {
        e.preventDefault();
        if (cnum == ctotal) return false;
        c(cnum+1);
    });
    u(unum);
    $('#uprevious').click(function (e) {
        e.preventDefault();
        if (unum == 1) return false;
        u(unum-1);
    });
    $('#unext').click(function (e) {
        e.preventDefault();
        if (unum == utotal) return false;
        u(unum+1);
    });
    $('#sprevious').click(function (e) {
        e.preventDefault();
        if (snum == 1) return false;
        s(snum-1);
    });
    $('#snext').click(function (e) {
        e.preventDefault();
        if (snum == stotal) return false;
        s(snum+1);
    });
    $('#qprevious').click(function (e) {
        e.preventDefault();
        if (qnum == 1) return false;
        q(qnum-1);
    });
    $('#qnext').click(function (e) {
        e.preventDefault();
        if (qnum == qtotal) return false;
        q(qnum+1);
    });
});
function c(num) {
    $.ajax({
        type: 'get',
        url: '/admin/getComplaints',
        data: {pageNum: num},
        headers: {
            'Authorization': window.sessionStorage.getItem("Authorization")
        },
        dataType: 'json',
        success: function (result) {
            var answered = $('#c');
            answered.html("");
            $.each(result.records, function (index, q) {
                var i = (num-1)*8+index+1;
                answered.append("<tr><th>" + i + "</th><td>" + q.uid + "</td><td>"+q.cid+"</td>"+
                    "<td>"+q.question+"</td><td>"+q.reason+"</td><td>"+
                    "<input type='text'  class='form-control' id='c"+q.id+"' placeholder='处理理由' style='display: inline-block;width: 60%'>" +
                    "<button class=\"btn btn-secondary\" onclick='handle("+q.id+")'>封禁</button>" +
                    "<button class=\"btn btn-info\" onclick='unhandle("+q.id+")'>忽略</button></td></tr>")
            });
            ctotal = result.pages;
            cnum = result.current;
        },
    })
}
function u(num) {
    $.ajax({
        type: 'get',
        url: '/admin/getUserInfos',
        data: {pageNum: num},
        headers: {
            'Authorization': window.sessionStorage.getItem("Authorization")
        },
        dataType: 'json',
        success: function (result) {
            var answered = $('#user');
            answered.html("");
            $.each(result.records, function (index, q) {
                var i = (num-1)*8+index+1;
                var active = q.active==true?"已激活":"未激活";
                var banned = q.banned==true?"已封禁":"未封禁";
                answered.append("<tr><th>" + i + "</th><td>" + q.username + "</td><td>"+q.email+"</td>"+
                    "<td>"+active+"</td><td>"+banned+"</td>"+
                    "<td>"+q.questionedNum+"</td><td>"+q.questionsNum+"</td><td>"+q.answerNum+"</td></tr>")
            });
            utotal = result.pages;
            unum = result.current;
        },
    })
}
function s(num) {
    var username = xssFilter($('#username').val());
    if(username==""){
        alert("查找内容不能为空!");
        return false;
    }
    var method = xssFilter($('#method').val());
    if(method==1){
        $.ajax({
            type: 'get',
            url: '/admin/getByUsername',
            data: {pageNum: num,username: username},
            headers: {
                'Authorization': window.sessionStorage.getItem("Authorization")
            },
            dataType: 'json',
            success: function (result) {
                var answered = $('#info');
                answered.html("");
                $.each(result.records, function (index, q) {
                    var i = (num-1)*1+index+1;
                    var active = q.active==true?"已激活":"未激活";
                    var banned = q.banned==true?"已封禁":"未封禁";
                    answered.append("<tr><th>" + i + "</th><td>" + q.username + "</td><td>"+q.email+"</td>"+
                        "<td>"+active+"</td><td>"+banned+"</td>"+
                        "<td>"+q.questionedNum+"</td><td>"+q.questionsNum+"</td><td>"+q.answerNum+"</td>" +
                        "<td style='padding-top: 0'><button class=\"btn btn-secondary my-2 my-sm-0\" onclick='ban("+q.uid+")'>封禁</button>" +
                        "<button class=\"btn btn-success my-2 my-sm-0\" onclick='unban("+q.uid+")'>解封</button>"+
                        "<select class=\"form-control my-2 mr-sm-2\" id=\"t"+q.uid+"\" style=\"display: inline-block;width: 50%;\">\n" +
                        "<option selected value=\"1\">请选择查看内容</option>\n" +
                        "<option value=\"1\">收到的提问</option>\n" +
                        "<option value=\"2\">回复的提问</option>\n" +
                        "<option value=\"3\">发起的提问</option>\n" +
                        "</select><button class=\"btn btn-primary my-2 my-sm-0\" onclick='find("+q.uid+")'>查看"+
                        "</button></td></tr>")
                });
                stotal = result.pages;
                snum = result.current;
            },
        })
    }else {
        $.ajax({
            type: 'get',
            url: '/admin/getByEmail',
            data: {pageNum: num,email: username},
            headers: {
                'Authorization': window.sessionStorage.getItem("Authorization")
            },
            dataType: 'json',
            success: function (result) {
                var answered = $('#info');
                answered.html("");
                $.each(result.records, function (index, q) {
                    var i = (num-1)*3+index+1;
                    var active = q.active==true?"已激活":"未激活";
                    var banned = q.banned==true?"已封禁":"未封禁";
                    answered.append("<tr><th>" + i + "</th><td>" + q.username + "</td><td>"+q.email+"</td>"+
                        "<td>"+active+"</td><td>"+banned+"</td>"+
                        "<td>"+q.questionedNum+"</td><td>"+q.questionsNum+"</td><td>"+q.answerNum+"</td>" +
                        "<td style='padding-top: 0'><button class=\"btn btn-secondary my-2 my-sm-0\" onclick='ban("+q.uid+")'>封禁</button>" +
                        "<button class=\"btn btn-success my-2 my-sm-0\" onclick='unban("+q.uid+")'>解封</button>"+
                        "<select class=\"form-control my-2 mr-sm-2\" id=\"t"+q.uid+"\" style=\"display: inline-block;width: 50%;\">\n" +
                        "<option selected value=\"1\">请选择查看内容</option>\n" +
                        "<option value=\"1\">收到的提问</option>\n" +
                        "<option value=\"2\">回复的提问</option>\n" +
                        "<option value=\"3\">发起的提问</option>\n" +
                        "</select><button class=\"btn btn-primary my-2 my-sm-0\" onclick='find("+q.uid+")'>查看"+
                        "</button></td></tr>")
                });
                stotal = result.pages;
                snum = result.current;
            },
        })
    }
}
function find(id) {
    $('#hi').css("display","");
    uid = id;
    q(1);
}
function q(num) {
    var type = xssFilter($('#t'+uid).val());
    var url;
    if(type==1){
        url="/admin/getQuestions";
    }else if(type==2){
        url="/admin/getAnswered";
    }else {
        url="/admin/getSended";
    }
    $.ajax({
        type: 'get',
        url: url,
        data: {pageNum: num,id: uid},
        headers: {
            'Authorization': window.sessionStorage.getItem("Authorization")
        },
        dataType: 'json',
        success: function (result) {
            var answered = $('#q');
            answered.html("");
            $.each(result.records, function (index, qu) {
                var i = (num-1)*3+index+1;
                answered.append("<tr><th>" + i + "</th><td>" + qu.username + "</td><td>"+qu.email+"</td>"+
                    "<td>"+qu.ausername+"</td><td>"+qu.aemail+"</td><td>"+qu.content+"</td><td>"+qu.answer+"</td></tr>")
            });
            qtotal = result.pages;
            qnum = result.current;
        },
    })
}
function handle(id) {
    if(confirm("你确定要封禁该用户吗？")){
        var reason  =  ($('#c'+id).val());
    if(reason==""){
        alert("处理原因不能为空");
        return false;
    }
    $.ajax({
        type: 'put',
        url: '/admin/handle',
        data: {cid: id,result: reason},
        headers: {
            'Authorization': window.sessionStorage.getItem("Authorization")
        },
        dataType: 'json',
        success: function (result) {
            if(result.code==200){
                alert(result.msg);
                c(cnum);
                u(unum);
            }else alert(result.msg);
        },
        error: function () {
            alert("出现错误!");
        }
    })}
}
function unhandle(id) {
    if(confirm("你确定要忽略该条举报吗？该行为不可恢复！")){var reason  =  xssFilter($('#c'+id).val());
    $.ajax({
        type: 'delete',
        url: '/admin/unhandle',
        data: {cid: id},
        headers: {
            'Authorization': window.sessionStorage.getItem("Authorization")
        },
        dataType: 'json',
        success: function (result) {
            if(result.code==200){
                alert(result.msg);
                c(cnum);
                u(unum);
            }else alert(result.msg);
        },
        error: function () {
            alert("出现错误!");
        }
    })}
}
function ban(uid) {
    if(confirm("你确定要封禁该用户吗？")){
        $.ajax({
            type: 'post',
            url: '/admin/ban',
            data: {uid: uid},
            headers: {
                'Authorization': window.sessionStorage.getItem("Authorization")
            },
            dataType: 'json',
            success: function (result) {
                if(result.code==200){
                    alert(result.msg);
                    u(unum);
                    s(snum);
                    $('#hi').css("display","none");
                }else alert(result.msg);
            },
            error: function () {
                alert("出现错误!");
            }
        })}
}
function unban(uid) {
    if(confirm("你确定要解禁该用户吗？")){
        $.ajax({
            type: 'post',
            url: '/admin/unban',
            data: {uid: uid},
            headers: {
                'Authorization': window.sessionStorage.getItem("Authorization")
            },
            dataType: 'json',
            success: function (result) {
                if(result.code==200){
                    alert(result.msg);
                    u(unum);
                    s(snum);
                    $('#hi').css("display","none");
                }else alert(result.msg);
            },
            error: function () {
                alert("出现错误!");
            }
        })}
}
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