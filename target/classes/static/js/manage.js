var anum = 1;
var atotal = 1;
var unum = 1;
var utotal = 1;
var dnum = 1;
var dtotal = 1;
var bnum = 1;
var btotal = 1;
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
    a(anum);
    $('#aprevious').click(function (e) {
        e.preventDefault();
        if (anum == 1) return false;
        a(anum-1);
    });
    $('#anext').click(function (e) {
        e.preventDefault();
        if (anum == atotal) return false;
        a(anum+1);
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
    d(dnum);
    $('#dprevious').click(function (e) {
        e.preventDefault();
        if (dnum == 1) return false;
        d(dnum-1);
    });
    $('#dnext').click(function (e) {
        e.preventDefault();
        if (dnum == dtotal) return false;
        d(dnum+1);
    });
    b(bnum);
    $('#bprevious').click(function (e) {
        e.preventDefault();
        if (bnum == 1) return false;
        b(bnum-1);
    });
    $('#bnext').click(function (e) {
        e.preventDefault();
        if (bnum == btotal) return false;
        b(bnum+1);
    });
});
function a(num) {
    $.ajax({
        type: 'post',
        url: '/question/getMyAnswered',
        data: {pageNum: num},
        headers: {
            'Authorization': window.sessionStorage.getItem("Authorization")
        },
        dataType: 'json',
        success: function (result) {
            var answered = $('#a');
            answered.html("");
            $.each(result.records, function (index, q) {
                var i = (num-1)*8+index+1;
                answered.append("<tr><th>" + i + "</th><td>" + q.content + "</td><td>"+
                    "<input type='text' style='width: 100%' class='form-control'  onchange='change(this,"+q.id+")' value='"+q.answer+"'></td><td>" +
                    "<button class=\"btn btn-secondary\" onclick='del("+q.id+")'>删除</button>" +
                    "<button class=\"btn btn-dark\" onclick='block("+q.id+")'>拉黑</button>" +
                    "<input class='form-control' id='c"+q.id+"' placeholder='举报理由' type='text' style='margin-left: 10px;width: 50%;display: inline-block'>"+
                    "<button class=\"btn btn-warning\" onclick='complain("+q.id+",xssFilter($(\"#c"+q.id+"\").val()))'>举报</button>" +
                    "</td></tr>");
            });
            atotal = result.pages;
            anum = result.current;
        },
    })
}
function u(num) {
    $.ajax({
        type: 'post',
        url: '/question/getUnanswered',
        data: {pageNum: num},
        headers: {
            'Authorization': window.sessionStorage.getItem("Authorization")
        },
        dataType: 'json',
        success: function (result) {
            var answered = $('#u');
            answered.html("");
            $.each(result.records, function (index, q) {
                var i = (num-1)*8+index+1;
                answered.append("<tr><th>" + i + "</th><td>" + q.content + "</td><td>"+
                    "<input type='text' style='width: 100%' class='form-control'  onchange='change(this,"+q.id+")' value='"+q.answer+"'></input></td><td>" +
                    "<button class=\"btn btn-secondary\" onclick='del("+q.id+")'>删除</button>" +
                    "<button class=\"btn btn-dark\" onclick='block("+q.id+")'>拉黑</button>" +
                    "<input class='form-control' id='c"+q.id+"' placeholder='举报理由' type='text' style='margin-left: 10px;width: 50%;display: inline-block'>"+
                    "<button class=\"btn btn-warning\" onclick='complain("+q.id+",xssFilter($(\"#c"+q.id+"\").val()))'>举报</button>" +
                    "</td></tr>");
            });
            unum = result.current;
            utotal = result.pages;
        },
    })
}
function d(num) {
    $.ajax({
        type: 'post',
        url: '/question/getDeleted',
        data: {pageNum: num},
        headers: {
            'Authorization': window.sessionStorage.getItem("Authorization")
        },
        dataType: 'json',
        success: function (result) {
            var answered = $('#d');
            answered.html("");
            $.each(result.records, function (index, q) {
                var i = (num-1)*8+index+1;
                answered.append("<tr><th>" + i + "</th><td>" + q.content + "</td><td style='width: 40%'>" + q.answer + "</td><td>" +
                    "<button class=\"btn btn-primary\" onclick='recover("+q.id+")'>恢复</button>" +
                    "<button class=\"btn btn-dark\" onclick='block("+q.id+")'>拉黑</button>" +
                    "<input class='form-control' id='c"+q.id+"' placeholder='举报理由' type='text' style='margin-left: 10px;width: 50%;display: inline-block'>"+
                    "<button class=\"btn btn-warning\" onclick='complain("+q.id+",xssFilter($(\"#c"+q.id+"\").val()))'>举报</button>" +
                    "</td></tr>");
            });
            dnum = result.current;
            dtotal = result.pages;
        },
    })
}
function b(num) {
    $.ajax({
        type: 'post',
        url: '/question/getBlocks',
        data: {pageNum: num},
        headers: {
            'Authorization': window.sessionStorage.getItem("Authorization")
        },
        dataType: 'json',
        success: function (result) {
            var answered = $('#b');
            answered.html("");
            $.each(result.records, function (index, q) {
                var i = (num-1)*8+index+1;
                answered.append("<tr><th>" + i + "</th><td style='width: 50%'>" + q.content + "</td><td>"+
                    "<button class=\"btn btn-primary\" onclick='unblock("+q.id+")'>取消拉黑</button></td></tr>");
            });
            bnum = result.current;
            btotal = result.pages;
        },
    })
}
function change(t,id) {
    var value = xssFilter($(t).val());
    $.ajax({
        url: "/question/changeAnswer",
        type: "post",
        data: {qid: id,answer: value},
        headers: {
            'Authorization': window.sessionStorage.getItem("Authorization")
        },
        success: function (result) {
            if(result.code==200){
                alert(result.msg);
                a(anum);
                u(unum);
            }else alert(result.msg);
        },
        error: function(){
            alert("出现错误!");
        }
    })
}
function del(id){
    if(confirm("你确定要删除该提问吗？")) {
        $.ajax({
            url: "/question/delete",
            type: "delete",
            data: {qid: id},
            headers: {
                'Authorization': window.sessionStorage.getItem("Authorization")
            },
            success: function (result) {
                if (result.code == 200) {
                    alert(result.msg);
                    a(anum);
                    u(unum);
                    d(dnum);
                } else alert(result.msg);
            },
            error: function () {
                alert("出现错误!");
            }
        })
    }
}
function recover(id) {
    if (confirm("你确定要恢复该提问吗？")) {
        $.ajax({
            url: "/question/recover",
            type: "post",
            data: {qid: id},
            headers: {
                'Authorization': window.sessionStorage.getItem("Authorization")
            },
            success: function (result) {
                if (result.code == 200) {
                    alert(result.msg);
                    a(anum);
                    u(unum);
                    d(dnum);
                } else alert(result.msg);
            },
            error: function () {
                alert("出现错误!");
            }
        })
    }
}
function complain(id,reason) {
    if(confirm("你确定要举报该用户吗？")) {
        if (reason == null) {
            alert("举报理由不能为空!");
            return false;
        }
        $.ajax({
            url: "/question/complain",
            type: "put",
            data: {qid: id, reason: reason},
            headers: {
                'Authorization': window.sessionStorage.getItem("Authorization")
            },
            success: function (result) {
                if (result.code == 200) {
                    alert(result.msg);
                    a(anum);
                    u(unum);
                    d(dnum);
                } else alert(result.msg);
            },
            error: function () {
                alert("出现错误!");
            }
        })
    }
};
function block(qid) {
    if(confirm("你确定要拉黑该提问的用户吗？")){
        $.ajax({
            url: "/question/block",
            type: "put",
            data: {qid: qid},
            headers: {
                'Authorization': window.sessionStorage.getItem("Authorization")
            },
            success: function (result) {
                if(result.code==200){
                    alert(result.msg);
                    a(anum);
                    u(unum);
                    d(dnum);
                    b(bnum);
                }else alert(result.msg);
            },
            error: function(){
                alert("出现错误!");
            }
        })
    }
}
function unblock(bid) {
    if(confirm("你确定要取消拉黑该提问的用户吗？")) {
        $.ajax({
            url: "/question/unblock",
            type: "delete",
            data: {bid: bid},
            headers: {
                'Authorization': window.sessionStorage.getItem("Authorization")
            },
            success: function (result) {
                if (result.code == 200) {
                    alert(result.msg);
                    b(bnum);
                    a(anum);
                    u(unum);
                    d(dnum);
                } else alert(result.msg);
            },
            error: function () {
                alert("出现错误!");
            }
        })
    }
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