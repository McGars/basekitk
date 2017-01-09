package com.mcgars.basekitk.tools.pagecontroller

/**
 * Created by Феофилактов on 16.07.2015.
 * Used from PageController

 * like
 * Page(key1 = "param1")
 * CustomFragment extend Fragment{}

 * equals
 * Bundle b = new Bundle()
 * b.putObject(key1(), val1);
 * b.putObject(key2(), val2);
 * fragment.setArguments(b);

 * use instead of
 * public static CustomFragment newInstance(Object val1, Object val2){
 * Bundle b = new Bundle()
 * b.putObject("param1", val1);
 * b.putObject("param2", val2);
 * fragment = new CustomFragment();
 * fragment.setArguments(b);
 * return fragment;
 * }
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
@kotlin.annotation.Retention()
annotation class Page(val key1: String = "", val key2: String = "", val key3: String = "")
