<p align="center">
<a href="https://www.manga2kindle.com/"><img src="https://www.manga2kindle.com/assets/media/hero.png" width="200px" alt="manga2kindle_logo"></a>
<h1 align="center" style="margin: 20px; text-align: center;">Manga2Kindle&nbsp;<a href="https://travis-ci.org/EduFdezSoy/Manga2Kindle"><img src="https://travis-ci.org/EduFdezSoy/Manga2Kindle.svg?branch=master" alt="Build Status"></a>
</h1></p>
<p align="center">
<i>The easiest way to move mangas to your e-reader.</i>
<br><br>
<a href="https://ko-fi.com/X8X0IK3C"><img src="https://www.ko-fi.com/img/githubbutton_sm.svg" alt="ko-fi"></a>
</p>
  
## Description
Manga2Kindle track the mangas at your phone and send them to your Kindle device.  
It allows you to read in your e-reader while using your favourite app to buy and download mangas.

## Usage
You can find guides and tutorials on how to use it at [manga2kindle.com](https://www.manga2kindle.com/)

## Compiling the app
If you plan to compile the app take a look at the Build Variants, you'll find three builds, both production builds (prod-) need a proper `google-services.json` so I recommend you to build it using the `mockDebug` variant, it will fake the file and you'll be able to play as you want (it won't report stats and crashes, that's all).

## Configuration
The only thing you need to change is the API url, you can find that in `M2KApplication` class as a constant called `BASE_URL`.  
Note that if you plan to run the app in any Android device with Pie (v9.0) or newer you'll need to use HTTPS, otherwise it won't work by default.  

## Dependencies
Library | Homepage
--- | ---
Room | [https://developer.android.com/jetpack/androidx/releases/room](https://developer.android.com/jetpack/androidx/releases/room)
Retrofit | [https://square.github.io/retrofit/](https://square.github.io/retrofit/)
Commons IO | [https://commons.apache.org/proper/commons-io/](https://commons.apache.org/proper/commons-io/)
MaterialDrawer | [https://mikepenz.github.io/MaterialDrawer/](https://mikepenz.github.io/MaterialDrawer/)
Android About Page | [https://github.com/medyo/android-about-page](https://github.com/medyo/android-about-page)
Conductor | [https://github.com/bluelinelabs/Conductor](https://github.com/bluelinelabs/Conductor)
Conductor Support Preference | [https://github.com/inorichi/conductor-support-preference](https://github.com/inorichi/conductor-support-preference)
Firebase | [https://firebase.google.com/](https://firebase.google.com/)
Crashlytics | [https://fabric.io/kits/android/crashlytics/](https://fabric.io/kits/android/crashlytics/)
Sweet Alert Dialog | [https://github.com/f0ris/sweet-alert-dialog](https://github.com/f0ris/sweet-alert-dialog)

*Last updated: v2.0-RC7*

## Donations
If you really liked it and feel like I deserve some money, you can buy me a [coffee](https://ko-fi.com/EduFdezSoy) and I'll continue transforming caffeine into code!  

## Copyright
Copyright &copy; 2019 Eduardo Fernandez.  

**Manga2Kindle** is released under a Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License; see _LICENSE_ for further details.
