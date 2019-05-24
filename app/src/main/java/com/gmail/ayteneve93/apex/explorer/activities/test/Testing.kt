package com.gmail.ayteneve93.apex.explorer.activities.test


/**
 * 딥 링킹 페이지 테스트 코드
    FirebaseDynamicLinks.getInstance()
    .getDynamicLink(intent)
    .addOnSuccessListener {
        it?.let {
                data ->
            Log.d("ayteneve93_test", data.link.toString())
        }
    }


    val a : JSONObject = JSONObject()
    a.put("test", "data");
    val dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
        .setLink(Uri.parse("http://date.jsontest.com/"))
        .setDomainUriPrefix("https://ayteneve93.page.link")
        .setAndroidParameters(DynamicLink.AndroidParameters.Builder("com.gmail.ayteneve93.apex.explorer").build())
        .buildDynamicLink()

    Log.d("ayteneve93_test", dynamicLink.uri.toString())
*/