$(document).ready(function () {
    var params = getUrlVars();
    console.log(params);

    if (params && params.length > 0) {
        GetResults(params.query);
        $('#txtSearch').val(params.query);
    }

    $('#btnSearch').on('click', function(){
        GetResults($('#txtSearch').val());
    });
});

function GetResults(params) {
    $.ajax({
        'url': '/artist/searchArtist?name=' +params,
        'type': 'GET',
        'success': function (data) {
            console.log(data);
            var html = '';
             for(var i = 0; i < data.artist.length; i++){
                 html += '<div class="row"><div class="col-sm-12"><a href="artist.html?id=' + data.artist[i].ArtistId + '&name=' + data.artist[i].Name + '">' + data.artist[i].Name + '</a></div></div>';
             }
            $('#artistResults').html(html);
        }
    });

    $.ajax({
        'url': '/lyrics/findSongsWithWord?word=' +params,
        'type': 'GET',
        'success': function (data) {
            console.log(data);
            var html = '';
            for(var i = 0; i < data.songs.length; i++){
                html += '<div class="row"><div class="col-sm-12"><a href="song.html?id=' + data.songs[i].songId + '&name=' + data.songs[i].name + '&artist=' + data.songs[i].artistName + '">' + data.songs[i].name + '</a> by ' + data.songs[i].artistName + '</div></div>';
            }
            $('#lyricResults').html(html);
        }
    });


}