# PHP-to-Scala source code migration helper.
# See http://code.google.com/p/php-to-scala-migration-helper/ for details.
#
# Copyright(C) 2010 Alex T. Ramos / Zigabyte Corporation.
# COPYING is permitted under the terms of the GNU General Public License, v3.
#
# $Id: mklib.sh,v 1.5 2010-04-27 17:52:20 alex Exp $

cd ../../PHP_Transform_Lib/lib-src/resin-4.0.6/modules/quercus/src 

date=`date`
cat <<EOF
// generated by $0 on $date
package phplib;

import java.io.*;
import java.math.BigDecimal;
import javax.servlet.*;
import javax.servlet.http.*;

import com.caucho.vfs.*;
import com.caucho.quercus.*;
import com.caucho.quercus.env.*;
import com.caucho.quercus.annotation.*;
import com.caucho.quercus.lib.*;
import com.caucho.quercus.lib.curl.*;
import com.caucho.quercus.lib.date.*;
import com.caucho.quercus.lib.db.*;
import com.caucho.quercus.lib.dom.*;
import com.caucho.quercus.lib.file.*;
import com.caucho.quercus.lib.gettext.*;
import com.caucho.quercus.lib.i18n.*;
import com.caucho.quercus.lib.jms.*;
import com.caucho.quercus.lib.json.*;
import com.caucho.quercus.lib.mail.*;
import com.caucho.quercus.lib.mcrypt.*;
import com.caucho.quercus.lib.reflection.*;
import com.caucho.quercus.lib.regexp.*;
import com.caucho.quercus.lib.session.*;
import com.caucho.quercus.lib.simplexml.*;
import com.caucho.quercus.lib.spl.*;
import com.caucho.quercus.lib.string.*;
import com.caucho.quercus.lib.xml.*;
import com.caucho.quercus.lib.zip.*;
import com.caucho.quercus.lib.zlib.*;

import com.caucho.quercus.lib.ImageModule.QuercusImage;

public class phplib {

  private final Env quercus_context;
    
  public phplib(Env env) {
    this.quercus_context = env;
  } 

EOF

find . | egrep 'quercus/lib' | 
xargs perl -0777 -lne 'top:while(/public\s+static\s+(\w*)\s+(\w*)\s*\(\s*(.*?)\)[^\)]*\{/msg) {

  ($type,$name,$parms) = ($1,$2,$3);
  my @names = ();

  $parms =~ s/\@Reference\s*//g;
  $parms =~ s/\s*,\s*/,/g;
  $parms =~ s/ \[\]/\[\] /g;

  print "\n\n/* $ARGV : $name */\n";
  if ($parms =~ /^Env/) {
    @names = ("quercus_context");
    $parms =~ s/^Env\s+env,?\s*//;
  }
  elsif ($parms !~ /^(Value|double|StringValue)/) {
    next top;
  }
  
  $class = $ARGV;
  $class =~ s:^\./::;
  $class =~ s:\.java$::;
  $class =~ s:/:.:g;

  @names = (@names, map { /([\w\[\]]+)$/; $1 } (split(/\s*,\s*/, $parms)));
  $return = $type eq "void" ? "" : "return";
  
  if($name =~ /^(__construct|export|ob_gzhandler|encodeMime|decodeMime|create|stream_socket_client|stream_bucket_.*|decodeMimeHeaders|decodeEncode|createEregi?|createRegexp|getPluralExpr)$/) {
    print "/* EXCLUDED */\n";
    next top;
  }

  print "final public $type $name ($parms) {\n $return $class.$name (" . join(",", @names) . ");\n}\n";

  if($parms =~ /\@Optional/ && $parms !~ /\@Expect/) {
  
     my $defs = "";
     my @fparms = ();
     my @parms = split(/\s*,\s*/, $parms);
     foreach $p (@parms) {
       if($p =~ /^\s*\@Optional(?:\((.*?)\))?\s+([\w\[\]]*)\s+(\w*)/) {
	       ($odefault, $otype, $oname) = ($1, $2, $3);

	       if($otype =~ /int|long/ && $odefault =~ /^"([\-\d]+?)"$/) { 
	           $odefault =~ s/"(.*?)"/$1/;
	       }
	       elsif($otype eq "Value" && $odefault eq "") {
	           $odefault = "new ConstStringValue(\"\")";
	       } 
	       elsif($otype =~ /\[\]$/) {
	           $odefault = null;
	       }
	       else {
	           next top;
	       }
	       $defs .= "$otype $oname = $odefault;\n"; 
       }
       else {
         push @fparms, $p;
       }
     }
     $parms = join(", ", @fparms);
     print "final public $type $name ($parms) {\n $defs\n $return $class.$name (" . join(",", @names) . ");\n}\n";
  }

}'

echo "}"