/**
 * Internet Control Message Protocol for Java (ICMP4J)
 * http://www.icmp4j.org
 * Copyright 2009 and beyond, icmp4j
 * <p/>
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License version 3
 * as published by the Free Software Foundation as long as:
 * 1. You credit the original author somewhere within your product or website
 * 2. The credit is easily reachable and not burried deep
 * 3. Your end-user can easily see it
 * 4. You register your name (optional) and company/group/org name (required)
 * at http://www.icmp4j.org
 * 5. You do all of the above within 4 weeks of integrating this software
 * 6. You contribute feedback, fixes, and requests for features
 * <p/>
 * If/when you derive a commercial gain from using this software
 * please donate at http://www.icmp4j.org
 * <p/>
 * If prefer or require, contact the author specified above to:
 * 1. Release you from the above requirements
 * 2. Acquire a commercial license
 * 3. Purchase a support contract
 * 4. Request a different license
 * 5. Anything else
 * <p/>
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, similarly
 * to how this is described in the GNU Lesser General Public License.
 * <p/>
 * User: Sal Ingrilli
 * Date: May 25, 2014
 * Time: 6:19:51 PM
 */
 
###
### Project details
###
Sample code:
  import org.icmp4j.IcmpPingUtil;
  import org.icmp4j.IcmpPingRequest;
  import org.icmp4j.IcmpPingResponse;
  
  // request - use IcmpPingUtil.createIcmpPingRequest () to create a request with defaults
  final IcmpPingRequest request = IcmpPingUtil.createIcmpPingRequest ();
  request.setHost ("www.google.com");
  
  // delegate
  final IcmpPingResponse response = IcmpPingUtil.executePingRequest (request);
  
  // log
  final String formattedResponse = IcmpPingUtil.formatResponse (response);
  System.out.println (formattedResponse);
  
See it in action:
  http://www.everyping.com/

Compilers:
  Built with Sun Java 1.7.0_55-b13, with a source/target combination of 1.6
  Apache Ant 1.9.2

Binaries:
  jna-3.5.1.jar (677 KB)
  platform-3.5.1.jar (931 KB)
  icmp4j.jar (23 KB)
  
Tested platforms:
  Windows 8.1 64-bit 
  Windows Server 2008r2 64-bit 
  Windows 7 Pro 64-bit (ver 6.1.7601)
  Windows XP Pro 32-bit
  Debian 6
  Mint17
  ArchLinux
  OSX 10.11.3
  Ubuntu 15.10 (kernel 4.2.0) 64 bit
  Ubuntu 15.10 (kernel 4.2.0) 32 bit   
  
Running from the command line:
  1. mkdir c:\temp\icmp4j
  2. cd c:\temp\icmp4j
  3. Place icmp4j-project.zip in c:\temp\icmp4j
  4. Expand icmp4j-project.zip in place
  5. cd c:\temp\icmp4j\trunk\icmp4j\output\tool
  4. java -cp * org.icmp4j.tool.Ping www.google.com
     -or, if the above does not work:
  4. java -cp jna-3.5.1.jar;platform-3.5.1.jar;icmp4j.jar org.icmp4j.tool.Ping www.google.com

Using icmp4j with native libraries on unix platforms:
  ICMP EchoReply native access can be done either using JNI or JNA calls.
  icmp4j-project.zip contains compiled dynamic libraries for OSX 10.11 and Linux (32 and 64 bit)
  
  1. Expand icmp4j-project.zip to the directory of your choice
  2. in the trunk/platform/linux/release, look for the library matching your architecture.
     - for linux distribution (32 bit) copy libicmp4jJNI_32bit.so and libicmp4jJNA_32bit.so to your deployment directory.
       Rename libicmp4jJNI_32bit.so to libicmp4jJNI.so and libicmp4jJNA_32bit.so to libicmp4jJNA.so
     
     - for linux distribution (64 bit) copy libicmp4jJNI_64bit.so and libicmp4jJNA_64bit.so to your deployment directory.
       Rename libicmp4jJNI_64bit.so to libicmp4jJNI.so and libicmp4jJNA_64bit.so to libicmp4jJNA.so

     - for OSX copy libicmp4jJNA.dylib to your deployment directory

  3.JNI mode
    java -cp icmp4j.jar -Djava.libraty.path=<path to your library> org.icmp4j.tool.Ping www.google.com
    
    JNA mode 
    java -cp jna-3.5.1.jar;platform-3.5.1.jar;icmp4j.jar -Djna.library.path=<path to your library> org.icmp4j.tool.Ping www.google.com

Recompiling native librairies:
You can recompile the libraries for your own platform.
The source code and the makefile are located in trunk/platform/unix/source in the icmp4j-project.zip file.
 
    
Credits:
1. shortpasta-icmp, the predecessors of icmp4j
2. Haiming Zhang, 64-bit versions of the dll (most recent build)
3. Tiberius Pircalabu, 64-bit versions of the dll (initial builds)
4. Damian Fernandez, reported bug with shortpasta-icmp.dll and sping.exe that can generate the GPF when running in non-administrative mode
5. Jun Kwang, help with testing IcmpPingTool
6. Kevin Shih: Help with testing and integration
7. Nucly: add Mint17 and ArchLinux support
8. Dekker: cooperate on Android support
9. Daifeisg8: Icmp4jUtil.nativeBridge initialization bug
10. Laurent Buhler: *nix and mac platform native implementations

###
### Date: monday 2016-02-8 12:00:00
### Build: 1020
###
-- [BUG]
  Fix a memory corruption in the JNI implementation
  Fix error message returned when TTL exceeded

###
### Date: Wednesday 2016-01-26 10:55:00
### Build: 1019
###
-- [FEATURE]
  Add native code for *nix platforms, using JNI or JNA call

-- [FEATURE]
  Add icmp4j-all.jar, which includes all distributions and native files.
  If you run on Windows/Linux/Mac/Solaris, you should be able to import only this jar
  and icmp4j will extract the required native files from its own jar at runtime.
  The temp files are extracted to %user.home%/icmp4j.
  These files are deleted on exit, unless the JVM dies.

-- [BUG]
  Fix MacProcessNativeBridge parameter when calling ping command

###
### Date: Wednesday 2015-08-12 10:55:00
### Build: 1018
###
-- [FEATURE]
  Add dependency-less abstraction so that we can compile without DLLs for Android, courtesy dekker
  dekker will be putting up his Android app on the store one of these days...
  For android, use icmp4j-android.jar instead of icmp4j.jar

-- [BUG]
  Fix Icmp4jUtil.nativeBridge initialization bug, courtesy daifeisg8
  The NativeBridge attribute should have been set after being fully initialized.

###
### Date: Friday 2014-10-31 14:53:00
### Build: 1017
###
-- [FEATURE]
  Add Mint17 and ArchLinux support, courtesy Nucly.
  These Linux distributions use the text "icmp_seq" and NOT "icmp_req" for the sequence number.
  Example:
    40 bytes from lax02s02-in-f19.1e100.net (74.125.224.211): icmp_seq=1 ttl=56 time=47.2 ms

###
### Date: Thursday 2014-09-04 14:42:00
### Build: 1016
###
-- [FEATURE]
  Add async support.
  Invoke this method to execute the request asynchronously and invoke you when completed:

    public static void executePingRequest (
      final IcmpPingRequest request,
      final AsyncCallback<IcmpPingResponse> asyncCallback);

-- [FEATURE]
  Add src distribution

-- [FEATURE]
  Add executePingRequests ().
  Invoke this method to execute several requests and receive the responses when all are completed.

    public static List<IcmpPingResponse> executePingRequests (
        final IcmpPingRequest request,
        final int packetCount)

###
### Date: Sunday 2014-05-25 14:38:00
### Build: 1015
###
-- [FEATURE]
  First implementation with jna/native support for Windows, all platforms, all architectures

    public static IcmpPingResponse executePingRequest (final IcmpPingRequest request);