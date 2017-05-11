$(document).ready(function () {
    getArtists();

    $('#btnNewArtist').on('click', function(){
        addArtist($('#txtNewArtist').val());
    })

    $('#artists').on('click','.btnDelete', function(){
        var id = $(this).attr('id');
        $.ajax({
            'url': '/artist/deleteArtist?id=' + id,
            'type': 'POST',
            'success': function(data){
                getArtists();
            }
        });
    });

});

function getArtists(){
    $.ajax({
        'url': '/artist/findAllJson',
        'type': 'GET',
        'success': function (data) {
            console.log(data);
            var html = '';
            for(var i = 0; i < data.artists.length; i++){
                html += '<div class="row"><div class="col-sm-12"><a id="' + data.artists[i].ArtistId + '" class="btnDelete btn-link "><span class="glyphicon glyphicon-trash"></span></a>&nbsp;&nbsp;&nbsp;<a href="artist.html?id=' + data.artists[i].ArtistId + '&name=' + data.artists[i].Name + '">' + data.artists[i].Name + '</a></div></div>';
            }
            $('#artists').html(html);
        }
    });
}

function addArtist(params){
    $.ajax({
        'url': '/artist/insert?name=' + params,
        'type': 'POST',
        'success': function(data){
            getArtists();
        }
    });
}
