$('.login').eq(0).off('click').on('click', function(){
    login();
});
function login() {
    var authorName = $('.user-name').eq(0).val();
    var password = $('.password').eq(0).val();
    var url = '/login?authorName=' + authorName + '&password=' + password;
    var xhr = new XMLHttpRequest();
    xhr.open('POST', url, true);
    xhr.onreadystatechange = function() {
        if(xhr.readyState == 4 && xhr.status == 200) {
            var response = xhr.responseText;
            alert(response); 
            if(response.indexOf('SUCCESS') != -1 ) window.location.href = '/author/author.html?authorId=' + response.split('?')[1].split('=')[1];
        }
    }
    xhr.send();
}