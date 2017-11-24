# CanlendarObserver
#需求分析:

本人现在任职于 [日事清](https://www.rishiqing.com/),由于我们的禁品包括滴答清单等，看到禁品有对应的功能，是吧，我们也不能掉链子，那么需求就来了，照着他们做，他们有什么我们有什么，那么到底是什么呢？

#效果图展示

![](https://github.com/senlinxuefeng/CalendarObserver/screenshorcuts/6666.gif)<br>

##技术前言

遇到查询系统联系人的一个问题，对query()方法理解的不到位，现在总结整理一下！

解释：

       ContentResolver contentResolver = this.getContentResolver();

ContentResolver直译为内容解析器，Android中程序间数据的共享是通过Provider/Resolver进行的。提供数据（内容）的就叫Provider，Resovler提供接口对这个内容进行解读。

在这里，系统提供了联系人的Provider，那么我们就需要构建一个Resolver来读取联系人的内容。

    Cursor cursor = contentResolver.query(android.provider.ContactsContract.Contacts.CONTENT_URI,
                    null, null, null, null);
                
android官方文档： 

    public final Cursor query (Uri uri, String[] projection,String selection,String[] selectionArgs, String sortOrder)

第一个参数，uri，上面我们提到了Android提供内容的叫Provider，那么在Android中怎么区分各个Provider？有提供联系人的，有提供图片的等等。所以就需要有一个唯一的标识来标识这个Provider，Uri就是这个标识，
android.provider.ContactsContract.Contacts.CONTENT_URI就是提供联系人的内容提供者，可惜这个内容提供者提供的数据很少。

第二个参数，projection，真不知道为什么要用这个单词，这个参数告诉Provider要返回的内容（列Column），比如Contacts Provider提供了联系人的ID和联系人的NAME等内容，如果我们只需要NAME，那么我们就应该使用：

    Cursor cursor = contentResolver.query(android.provider.ContactsContract.Contacts.CONTENT_URI,
        new String[]{android.provider.ContactsContract.Contacts.DISPLAY_NAME}, null, null, null);
    
当然，下面打印的你就只能显示NAME了，因为你返回的结果不包含ID。用null表示返回Provider的所有内容（列Column）。

第三个参数，selection，设置条件，相当于SQL语句中的where。null表示不进行筛选。如果我们只想返回名称为张三的数据，第三个参数应该设置为：

    Cursor cursor = contentResolver.query(android.provider.ContactsContract.Contacts.CONTENT_URI,
        new String[]{android.provider.ContactsContract.Contacts.DISPLAY_NAME},
        android.provider.ContactsContract.Contacts.DISPLAY_NAME + "='张三'", null, null);
    
第四个参数，selectionArgs，这个参数是要配合第三个参数使用的，如果你在第三个参数里面有？，那么你在selectionArgs写的数据就会替换掉？

    Cursor cursor = contentResolver.query(android.provider.ContactsContract.Contacts.CONTENT_URI,  
        new String[]{android.provider.ContactsContract.Contacts.DISPLAY_NAME},  
        android.provider.ContactsContract.Contacts.DISPLAY_NAME + "=?",  
                    new String[]{"张三"}, null);  
                    
效果和上面一句的效果一样。

第五个参数，sortOrder，按照什么进行排序，相当于SQL语句中的Order by。如果想要结果按照ID的降序排列：

    Cursor cursor = contentResolver.query(android.provider.ContactsContract.Contacts.CONTENT_URI,
                    null, null,null, android.provider.ContactsContract.Contacts._ID + " DESC");

 "DESC"降序，其实默认排序是升序，+"ASC"写不写效果都一样.

