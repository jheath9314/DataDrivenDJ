<!doctype html>
<html>
<head>
    <title>Artists</title>
</head>
<body>
Artists:
<g:each var="artist" in="${artists}">
    <ol>
<li>Artist: ${artist.name} (artistId=${artist.artistId}) </li>
    </ol>
</g:each>
</body>
</html>
