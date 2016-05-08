# GalleryView
 A widget like gallery which base on RecyclerView
##how to use?

1.set the GalleryView layout xml,like this
*must set the app:itemWidth 
```xml
  <org.net.sunger.widget.GalleryView
        app:itemWidth="150dp"
         android:id="@+id/recyclerview"
         android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:background="@android:color/transparent"
        android:layout_centerVertical="true"
        android:layout_alignParentLeft="true"
        android:layout_alignParentStart="true"></org.net.sunger.widget.GalleryView>
```
2.set the item layout xml
*set the width must be a accurate numerical value

3.callback

```java
     mGallery.setOnItemSelectedListener(new GalleryView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int position) {
 
            }
        });
```
## License

```
Copyright 2016 sungerk

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```




