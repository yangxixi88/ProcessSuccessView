# ProcessSuccessView
高仿小米"安全中心"中垃圾清理成功后的显示界面

详细用法见：http://www.jianshu.com/p/604a6976f82b

![][viewgif]
[viewgif]:https://github.com/yangxixi88/ProcessSuccessView/blob/master/cleanAnimotion.gif

以下的参数都可以自行定义，很大程度可以DIY。欢迎Star!!
```
<declare-styleable name="ProcessSuccessView">
        <!--圆圈屏幕占比 0~1 小于0是0  超过1算1-->
        <attr name="scale" format="float"/>
        <!--圆圈弧度的颜色-->
        <attr name="arcColor" format="color"/>
        <!--中间钩子的颜色-->
        <attr name="rightColor" format="color"/>
        <!--圆弧的宽度-->
        <attr name="arcWidth" format="dimension"/>
        <!--钩子的宽度-->
        <attr name="rightWidth" format="dimension"/>
        <!--是否展示旁边的三角形晃动-->
        <attr name="isShowTriAngle" format="enum">
            <enum name="hidden" value="0"/>
            <enum name="show" value="1"/>
        </attr>
</declare-styleable>
```