var blogId = window.location.href.split('?')[1].split('=')[1];
var comments;

getAllComments();
setTitle();

function setTitle() {
    var title = $('.blog-title').eq(0).text()
    if(title == '') title = 'blog';
    $('title').eq(0).text(title);
}


function sendAjax(url, need) {
    var xhr = new XMLHttpRequest();
    xhr.open('POST', url, true);
    xhr.onreadystatechange = function (){
        if(xhr.readyState == 4 && xhr.status == 200) {
            if(need == 'comments') {
                comments = xhr.responseText;
                showComments(JSON.parse(comments));
            }else if(need == 'writeComment') {
                alert(xhr.responseText);
                window.location.href = window.location.href;
            }
        }
    }
    xhr.send();
}

function getAllComments() {
    sendAjax('/allComments?blogId=' + blogId, 'comments');
}

function showComments(commentsJSONArray) {
    var commentWrapper = $('.comment-wrapper').eq(0);
    commentsJSONArray.forEach(function(ele, index) {
        var date = new Date(ele.ctime);
        var time = date.getFullYear() + '/' + date.getMonth() + 1 + '/' + date.getDate() + ' ' + date.getHours() + '：' + date.getMinutes() + '：' + date.getSeconds();
        var theComment = $('.tpl').clone().removeClass('tpl');
        theComment
            .find('.user-name')
                .eq(0)
                    .text(ele.userName)
                        .next()
                            .text(ele.userEmail)
                                .next()
                                    .text(time)
                                        .next()
                                            .text(ele.content);
        commentWrapper.append(theComment);
    });
}

function canSubmit() {
    if(isOverstep($('.user-name').eq(0).val(),16)) {
        alert('昵称不能超过16个字符');
        return false;
    }else if(!isEmail($('.user-email').eq(0).val())) {
        alert('邮箱格式错误');
        return false
    }else if(isOverstep($('.user-email').eq(0).val(), 64)) {
        alert('邮箱不能超过64个字符');
        return false;
    }else if(isOverstep($('textarea').eq(0).val(), 256)) {
        alert('留言内容不能超过256个字符');
        return false;
    }
    return true;
}

function submit() {
    if(!canSubmit()) return;
    var userName = $('.user-name').eq(0).val();
    var userEmail = $('.user-email').eq(0).val();
    var content = $('textarea').eq(0).val();
    var url = '/writeComment?userName=' + userName + '&userEmail=' + userEmail + '&content=' + content + '&blogId=' + blogId;
    sendAjax(url, 'writeComment');
}

function isEmail(msg) {
    console.log(msg);
    var reg = /^[0-9a-zA-Z_.-]+[@][0-9a-zA-Z_.-]+([.][a-zA-Z]{2,4})$/;
    return reg.test(msg);
}

function isOverstep(msg, length) {
    var count = msg.length;
    if(count > length) return true;
    for(var i = 0; i < msg.length; i ++) {
        if(msg.charCodeAt(i) > 255) count ++;
        if(count > length) return true;
    }
    return false;
}

$('.submit').eq(0).off('click').on('click', function(){
    submit();
});