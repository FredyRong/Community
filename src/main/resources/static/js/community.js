/**
 提交回复
 **/
function post() {
    let questionId = $("#question_id").val();
    let content = $("#comment_content").val();
    comment2target(questionId, 1, content);
}

function comment2target(targetId, type, content) {
    if (!content) {
        alert("不能回复空内容~~");
        return;
    }
    $.ajax({
        type: "POST",
        url: "/comment",
        contentType: "application/json",
        data: JSON.stringify({
            "parentId": targetId,
            "content": content,
            "type": type
        }),
        success: function (response) {
            if (response.code == 200) {
                window.location.reload();
            } else if (response.code == 2003) {
                let isAccepted = confirm(response.message);
                if (isAccepted) {
                    window.open("https://github.com/login/oauth/authorize?client_id=Iv1.78ff0dfff5b4f546&redirect_uri=http://localhost:8887/callback&state=1");
                    window.localStorage.setItem("closable", "true");
                }
            } else {
                alert(response.message);
            }
        },
        dataType: "json"
    })
}

function comment(e) {
    let span = $(e);
    let commentId = span.data("id");
    let content = $("#input-" + commentId).val();
    comment2target(commentId, 2, content);
}

/**
 * 展开二级评论
 */
function collapseComments(e) {
    let span = $(e);
    let id = span.data("id");
    let comment = $("#comment-" + id);
    if (comment.hasClass("in")) {
        comment.removeClass("in");
        span.removeClass("icon_active");
    } else {
        $.getJSON("/comment/" + id, function (data) {
            let commentBody = $("#comment-body-" + id);
            let items = [];

            $.each(data.data, function (comment) {
                let c = $("<div>", {
                    "class": "col-lg-12 col-md-12 col-sm-12 col-xs-12 comment",
                    html: comment.content
                });
                items.push(c);
            });
            $("<div>", {
                "class": "col-lg-12 col-md-12 col-sm-12 col-xs-12 collapse sub-comments",
                "id": "comment-" + id,
                html: items.join("")
            }).appendTo(commentBody);

            comment.addClass("in");
            span.addClass("icon_active");
        })
    }
}
