<?php
include('config.php');
    
if (isset($_POST['root'])){
    
    $db_addr = $db_url . ":" . $db_port;
    
    $chost = "localhost";

    $user = $_POST['root'];
    $pass = $_POST['pass'];
    
    $server = mysql_connect( $db_addr, $user, $pass);
    
    if(!$server){
        die("<html><head><title>Error connecting...</title></head><body>Could not connect to mysql server at $db_addr, using $user, check your configuration<BR>\nERROR: " . str_replace( "\n", "<BR>\n", mysql_error() ) . "</body></html>" );
    }    
    
    $mysql_query_listing = array(
    "SET SQL_MODE=\"NO_AUTO_VALUE_ON_ZERO\";",
    "CREATE DATABASE IF NOT EXISTS `$db_database` CHARACTER SET latin1 COLLATE latin1_bin;",    
    "CREATE USER '$db_user'@'$chost' IDENTIFIED BY '$db_pass';",
    "GRANT SELECT, INSERT, DELETE, UPDATE on `$db_database`.* TO '$db_user'@'$chost';",
    "USE `$db_database`;",
   
"CREATE TABLE IF NOT EXISTS `accelerometer_data` (
  `pid` bigint(20) NOT NULL,
  `uid` bigint(20) NOT NULL,
  `data_file_identifier` varchar(20) COLLATE latin1_bin NOT NULL,
  PRIMARY KEY (`pid`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COLLATE=latin1_bin;",

"CREATE TABLE IF NOT EXISTS `captcha` (
  `session_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `answer` varchar(10) COLLATE latin1_bin NOT NULL,
  `tried` tinyint(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (`session_id`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 COLLATE=latin1_bin AUTO_INCREMENT=37 ;",

"CREATE TABLE IF NOT EXISTS `plays` (
  `uid` int(11) NOT NULL,
  `winning` smallint(6) NOT NULL,
  `result` smallint(6) NOT NULL,
  `pid` bigint(20) NOT NULL AUTO_INCREMENT,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`pid`),
  KEY `uid` (`uid`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 COLLATE=latin1_bin AUTO_INCREMENT=81 ;",

"CREATE TABLE IF NOT EXISTS `sessions` (
  `sid` bigint(20) NOT NULL AUTO_INCREMENT,
  `uid` bigint(20) NOT NULL,
  `lucky` int(11) NOT NULL,
  `control` int(11) NOT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`sid`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 COLLATE=latin1_bin AUTO_INCREMENT=12 ;",

"CREATE TABLE IF NOT EXISTS `users` (
  `email` varchar(50) COLLATE latin1_bin NOT NULL,
  `password` varchar(32) COLLATE latin1_bin NOT NULL,
  `realname` text COLLATE latin1_bin NOT NULL,
  `uid` bigint(20) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`uid`),
  UNIQUE KEY `UNIQ_EMAIL` (`email`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 COLLATE=latin1_bin AUTO_INCREMENT=7 ;");

    echo "<html><head><title>Setting Up Database...</title></head><body><table width='800px'>";

    $i=0;
    foreach($mysql_query_listing as $query) {
        $result = mysql_query($query);
        $bgcolor='ffffff';
        if($i == 0){
            $bgcolor = '88ccff';
            $i=1;
        }else{
            $bgcolor = '99eeff';
            $i=0;
        }
        echo "<tr bgcolor='$bgcolor'><td>$query</td><td>";
        if(!$result){
            echo "FAILED!</td></tr>\n";
            die("</table><BR><BR>Could not proceed due to MYSQL error:" . str_replace("\n", "<BR>\n", mysql_error()) . "</body></html>");
        }
        echo "SUCCESS!</td></tr>\n";
    }
    echo "</table></body></html>";
    
}else{
    
    $script = "http://".$_SERVER['HTTP_HOST'].$_SERVER[PHP_SELF];

    ?>
    
    <html><head><title>Database Administrative Initialization</title></head><body>
    
    <form method="post" action = "<?php echo $script; ?>">

    <table width = '300px'>
    <tr bgcolor='88CCff'><td colspan='2'>Please enter mysql superuser credentials:</td></tr>
    <tr bgcolor='99EEff'><td>Username:</td><td><input type="text" name="root" /></td></tr>
    <tr bgcolor='88CCff'><td>Password:</td><td><input type="password" name="pass" /></td></tr>
    <tr align='right'><td></td><td width='100px'><input type="submit" value="Submit"/></td></tr>
    </table>
    </form>
    <?php
}


?>
