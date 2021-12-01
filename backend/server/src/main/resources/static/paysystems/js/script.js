$(function () {
    $(".select .head").click(function () {
        $(this).parent().toggleClass("opened");
    })

    $(".select li").click(function () {
        const $select = $(this).closest(".select");
        const $input = $select.find("input");
        $input.val(this.innerText);
        $select.find(".head").text(this.innerText);
        $select.removeClass("opened");
    })
})
