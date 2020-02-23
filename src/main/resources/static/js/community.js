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
        $(comment.children(":first")).remove();
    } else {
        let commentContainer = $("#comment-" + id);
        $.getJSON("/comment/" + id, function (data) {
            $.each(data.data.reverse(), function (index, comment) {
                let mediaLeftElement = $("<div/>", {
                    "class": "media-left"
                }).append($("<img/>", {
                    "class": "media-object img-rounded",
                    "src": comment.user.avatarUrl
                }));
                let mediaBodyElement = $("<div/>", {
                    "class": "media-body"
                }).append($("<h6/>", {
                    "class": "media-heading",
                    html: comment.user.name
                })).append($("<div/>", {
                    html: comment.content
                })).append($("<div/>", {
                    "class": "menu",
                }).append($("<span/>", {
                    "class": "pull-right",
                    html: moment(comment.gmtCreate).format("YYYY-MM-DD")
                })));

                let mediaElement = $("<div/>", {
                    "class": "media"
                }).append(mediaLeftElement).append(mediaBodyElement);

                let commentElement = $("<div/>", {
                    "class": "col-lg-12 col-md-12 col-sm-12 col-xs-12 comments"
                }).append(mediaElement);

                commentContainer.prepend(commentElement);
            });
            comment.addClass("in");
            span.addClass("icon_active");
        })
    }
}

function selectTag(e) {
    let value = $(e).data("tag");
    let previous = $("#tag").val();
    if(previous.indexOf(value) == -1){
        if (previous) {
            $("#tag").val(previous + ',' + value);
        }else{
            $("#tag").val(value);
        }
    }
}

function showSelectTag() {
    $("#select-tag").show();
    let li = $("#select-tag").children(":first").children(":first");
    li.addClass("active");
    $(li.children(":first")).attr("aria-expanded", "true");
    $(".tab-pane").eq(0).addClass("active");
}
