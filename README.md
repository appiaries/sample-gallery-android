Illustration Gallery App Demo for Appiaries (Android)
===========================

## About This App

Gallery App lets you add images with comments and later edit.  
All the images are stored as binary data on Appiaries server.  
Tapping "Play" button to play a slide-show of images registered.  
In "Settings" view, you may set whether to show/hide comments during the slide-show, or set its interval.

## Requirements

It does not require you an Appiaries account if you just want to build and run the app.  
Although it requires server-side data stored on Appiaries,  
it is already configured as default to retrieve the ones from our demo account.  
If you intend to customize the server-side data, you need a sign-up.  
Runs on Android 4.3 or higher.

## License

You may freely use, modify, or distribute the source codes provided.

## Appiaries API Services Used

* <a href="http://docs.appiaries.com/?p=11015&lang=en">JSON Data API</a>
* <a href="http://docs.appiaries.com/?p=11075&lang=en">File API</a>

## Appearance

<table>

<tr>
<td>
<b>Thumbnails List</b><br />
<img src="http://docs.appiaries.com/wordpress/wp-content/uploads/img/sample_gallery_shot_list.png">
</td>
<td>
<b>Detail</b><br />
<img src="http://docs.appiaries.com/wordpress/wp-content/uploads/img/sample_gallery_shot_list_detail.png">
</td>
<td>
<b>Edit</b><br />
<img src="http://docs.appiaries.com/wordpress/wp-content/uploads/img/sample_gallery_shot_edit.png">
</td>
</tr>

<tr>
<td>
<b>Add</b><br />
<img src="http://docs.appiaries.com/wordpress/wp-content/uploads/img/sample_gallery_shot_add.png">
</td>
<td>
<b>Add (Browse)</b><br />
<img src="http://docs.appiaries.com/wordpress/wp-content/uploads/img/sample_gallery_shot_add_browse.png">
</td>
<td></td>
</tr>

<tr>
<td colspan="3">
<b>Slide-show</b><br />
<img src="http://docs.appiaries.com/wordpress/wp-content/uploads/img/sample_gallery_shot_play.png">
</td>
</tr>

<tr>
<td>
<b>Settings</b><br />
<img src="http://docs.appiaries.com/wordpress/wp-content/uploads/img/sample_gallery_shot_setting.png">
</td>
<td></td>
<td></td>
</tr>

</table>


## Server-Side Collections Used

<table>

<tr>
<th>Entity</th>
<th>System Name</th>
<th>Type</th>
<th>Description</th>
<th>Note</th>
</tr>

<tr>
<td>Illustrations</td>
<td>Illustrations</td>
<td>JSON Collection</td>
<td>Stores the meta-information for illustration images uploaded.</td>
<td></td>
</tr>

<tr>
<td>Illustration Images</td>
<td>IllustrationImages</td>
<td>File Collection</td>
<td>Stores the actual image data associated with "Illustrations".</td>
<td></td>
</tr>

</table>


## Reference

For further details, refer to the official documents on Appiaries.

in English  
http://docs.appiaries.com/?p=13582&lang=en

in Japanese  
http://docs.appiaries.com/?p=13586

Also, iOS version available on GitHub.  
https://github.com/appiaries/sample-gallery-ios

Appiaries  
http://www.appiaries.com/
