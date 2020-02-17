function post() {
    let question_id = $("#question_id").val();
    let content = $("#comment_content").val();
    $.ajax({
        type: "POST",
        url: "/comment",
        contentType: "application/json",
        data: JSON.stringify({
          "parentId": question_id,
          "content": content,
          "type": 1
        }),
        success: function(response){
            if(response.code == 200){
                $("#comment_section").hide();
            }else if(response.code == 2003){
                let isAccepted = confirm(response.message);
                if(isAccepted){
                    window.open("https://github.com/login/oauth/authorize?client_id=Iv1.78ff0dfff5b4f546&redirect_uri=http://localhost:8887/callback&state=1");
                    window.localStorage.setItem("closable", "true");
                }
            }else{
                alert(response.message);
            }
        },
        dataType: "json"
    })
}