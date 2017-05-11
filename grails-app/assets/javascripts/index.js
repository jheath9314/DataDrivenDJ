$(document).ready(function () {
    $('#btnSearch').on('click', function () {
        var query = $('#txtSearch').val();
        window.location.replace('search.html?query=' + query);
    });
});

