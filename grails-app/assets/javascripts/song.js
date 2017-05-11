var lyrics;
var artist;
$(document).ready(function () {
    var params = getUrlVars();
    console.log(params);
    artist = params.artist;
    $('#songName').html(params.name + '<small> ' + artist + ' Lyrics</small>');
    $('#songNameTags').html(params.name + '<small> Tags</small>');
    getSongLyrics(params.id);
    getTags(params.id);

    $('#btnEdit').on('click', function(){
        console.log(lyrics);
        $('#lyrics').html('<div class="row"><div class="col-sm-12"><textarea id="txtLyrics" style="min-height: 400px" class="form-control" rows="3">' + lyrics + '</textarea></div></div><div class="row"><div class="col-sm-12"><br /><button id="btnSave" class="btn btn-primary pull-right">Save</button></div></div>');
    });

    $('#lyrics').on('click', '#btnSave', function(){
        $.ajax({
            'url': '/lyrics/update?songID=' + params.id + '&lyrics=' + $('#txtLyrics').val(),
            'type': 'POST',
            'success': function(data){
                getSongLyrics(params.id);
            }
        });
    });

    $('#btnNewTag').on('click', function(){
        addTag(params.id, $('#txtNewTag').val());
    })

    $("#tags").on('click', '.tag-upvote', function(){
        var tag = $(this).parent().attr('id');
        console.log(tag);
        $.ajax({
            'url': '/tag/upvoteTag?songId=' + params.id + '&tag=' + tag,
            'type': 'POST',
            'success': function(data){
                getTags(params.id);
            }
        });
    });

    $("#tags").on('click', '.tag-downvote', function(){
        var tag = $(this).parent().attr('id');
        console.log(tag);
        $.ajax({
            'url': '/tag/downvoteTag?songId=' + params.id + '&tag=' + tag,
            'type': 'POST',
            'success': function(data){
                getTags(params.id);
            }
        });
    });
});

function getSongLyrics(params) {
    $.ajax({
        'url': '/lyrics/getSong?songID=' + params,
        'type': 'GET',
        'success': function (data) {
            console.log(data);
            if(!data.lyrics){
                $.ajax({
                    'url': '/lyrics/insertSongLyrics?songID=' + params,
                    'type': 'GET',
                    'success': function (data) {
                        console.log(data);
                        $.ajax({
                            'url': '/lyrics/getSong?songID=' + params,
                            'type': 'GET',
                            'success': function (data) {
                                console.log(data);
                                $('#lyrics').html(data.lyrics.replace(/  /g, "<br/>"));
                                lyrics = data.lyrics;
                                $('#songName').html(data.name + '<small> <a href="artist.html?id=' + data.artistId + '&name=' + data.artistName + '">' + artist + '</a> Lyrics</small>');
                            }
                        });
                    }
                });
            } else {
                $('#lyrics').html(data.lyrics.replace(/  /g, "<br/>"));
                lyrics = data.lyrics;
                $('#songName').html(data.name + '<small> <a href="artist.html?id=' + data.artistId + '&name=' + data.artistName + '">' + artist + '</a> Lyrics</small>');
            }

        }
    });
}

function addTag(songId, tag){

    $.ajax({
        'url': '/tag/add?songId=' + songId + '&tag=' + tag,
        'type': 'POST',
        'success': function(data){
            getTags(songId);
        }
    });

}

function getTags(params) {
    $.ajax({
        'url': '/tag/list?songId=' + params,
        'type': 'GET',
        'success': function (data) {
            console.log(data.tags);
            var html = '';
            for ( var i = 0; i < data.tags.length; i++){
                html += '<h4 id="' + data.tags[i].Name + '"><span class="tag-upvote glyphicon glyphicon-arrow-up"></span><span class="tag-downvote glyphicon glyphicon-arrow-down"></span><span class="label label-primary">' + data.tags[i].Name  + '<span class="badge">' + data.tags[i].weight + ' </span></span></h4>'
            }
            $('#tags').html(html);
        }
    });
}