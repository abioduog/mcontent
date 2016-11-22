
<!--

Make changes to file:
/etc/php/7.0/cli/php.ini
...and put there document root where php-file is working
doc_root =/home/ubuntu1604/phptest

folder structure 

phptest
 - images (includes 3 pics)
 - firma1 (includes 2 pics)
 - firma2 (includes 2 pics)

Folder and starting...
ubuntu1604@ubuntu1604-VirtualBox:~/phptest$ pwd
/home/ubuntu1604/phptest
ubuntu1604@ubuntu1604-VirtualBox:~/phptest$ php -S localhost:8010


...and how to read pics
Url: http://localhost:8010/pics/firma1




-->
<html>
 <head>
  <title>PHP Test</title>
 </head>
 <body>
 <?php echo '<p>Load random image</p>'; ?> 
 </body>
<?php

echo "Request uri is: ".$_SERVER['REQUEST_URI'];
echo "<br>";

$hakemisto = $_SERVER['REQUEST_URI']."/*.*";
 
$rest = substr($hakemisto, 6); // Drop /pics/ from url
echo "Picture folder: ".substr($rest, 0, -4);
echo "<br>";

$files = glob("".$rest);

echo "Pics total in folder: ".count($files);
echo "<br/>";

$randomimage = rand(0,count($files) - 1);

$image = $files[$randomimage];
$supported_file = array(
    'gif',
    'jpg',
    'jpeg',
    'png'
);


$ext = strtolower(pathinfo($image, PATHINFO_EXTENSION));
if (in_array($ext, $supported_file)) {
    //print $image ."<br />";
    echo '<img src="../'.$image .'" alt="Random image" />'."<br /><br />";

} 

?>
</html>