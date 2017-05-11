var artist;
$(document).ready(function () {
    var params = getUrlVars();
    console.log(params);
    artist = params.artist;
    getAlbumSongs(params.id);
    getTags(params.id);
    $('#albumName').html(params.name + '<small> <a href="artist.html?id=' + params.artistId + '&name=' + artist + '">' + artist + '</a> Songs</small>');
    $('#albumNameTags').html(params.name + '<small> Tags</small>');
});

function getAlbumSongs(params) {
    $.ajax({
        'url': '/artist/findSongsByAlbumIdJson?albumId=' + params,
        'type': 'GET',
        'success': function (data) {
            console.log(data);
            var html = '';
            for (var i = 0; i < data.songs.length; i++) {
                html += '<div class="row"><div class="col-sm-12"><a href="song.html?id=' + data.songs[i].songId + '&name=' + data.songs[i].name + '&artist=' + artist + '">' + data.songs[i].name + '</a></div></div>';
            }
            $('#songs').html(html);
        }
    });
}

function getTags(params){
    $.ajax({
        'url': '/tag/topTagsAlbum?albumId=' + params,
        'type': 'GET',
        'success': function (data) {
            console.log(data.tags);
            var html = '';
            for ( var i = 0; i < data.tags.length; i++){
                html += '<h4><span class="label label-primary">' + data.tags[i].name  + '<span class="badge">' + data.tags[i].weight + ' </span></span></h4>'
            }
            $('#tags').html(html);
        }
    });
}