# Attributes

```xml
<!-- Default value: Color.GREEN-->
<attr name="bubbleColor" format="color" />

<!-- Default value: Color.RED -->
<attr name="bubbleThresholdColor" format="color" />

<!-- Default value: Color.BLACK -->
<attr name="outerCircleStrokeColor" format="color"/>

<!-- Default value: Color.BLACK -->
<attr name="innerCircleStrokeColor" format="color"/>

<!-- Default value: Color.BLACK -->
<attr name="crossStrokeColor" format="color"/>

<!-- Default value: 25 -->
<attr name="bubbleSize" format="float" />

<!-- Default value: 2.5 -->
<attr name="outerCircleStrokeWidth" format="float" />

<!-- Default value: 2.5 -->
<attr name="innerCircleStrokeWidth" format="float" />

<!-- Default value: 2.5 -->
<attr name="crossStrokeWidth" format="float" />

<!-- Default value: 150 -->
<attr name="bubbleInterpolationTimer" format="integer" />

<!-- Default value: true -->
<attr name="withThresholdIndication" format="boolean" />

<!-- Default value: 5 -->
<attr name="thresholdValue" format="float" />

<!-- Default value: true -->
<attr name="withLabel" format="boolean" />

<!-- Default value: 20 -->
<attr name="labelTextSize" format="float" />
```

# Usage

The SpiritiLevelView can be added directly to a XML-Layout or prgammatically instantiated and added to a parent view

## XML-Layout

```xml
<com.sample.rgregat.spiritlevelview.view.SpiritLevelView
	android:id="@+id/spiritLevelView"
        android:layout_width="300dp"
        android:layout_height="300dp"
        app:bubbleColor="@color/green500"
        app:bubbleInterpolationTimer="150"
        app:bubbleSize="25"
        app:bubbleThresholdColor="@color/deeporange500"
        app:crossStrokeColor="@color/gray500"
        app:crossStrokeWidth="2.5"
        app:innerCircleStrokeColor="@color/gray500"
        app:innerCircleStrokeWidth="2.5"
        app:labelTextSize="30"
        app:layout_constraintBottom_toTopOf="@+id/spiritLevelViewContainer"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:outerCircleStrokeColor="@color/black"
        app:outerCircleStrokeWidth="5"
        app:thresholdValue="5"
        app:withLabel="true"
        app:withThresholdIndication="true" />
```

## Programmatically

```kotlin
spiritLevelView = SpiritLevelView.Builder(this)
	.bubbleColor(getColor(R.color.blue500))
        .bubbleThresholdColor(getColor(R.color.pink500))
        .outerCircleStrokeColor(R.color.black)
        .innerCircleStrokeColor(R.color.gray500)
        .crossStrokeColor(R.color.gray500)
        .outerCircleStrokeWidth(5f)
        .innerCircleStrokeWidth(2.5f)
        .crossStrokeWidth(2.5f)
        .bubbleSize(10f)
        .withLabel(true)
        .labelTextSize(30f)
        .bubbleInterpolationTimer(150L)
        .withThresholdIndication(true)
        .thresholdValue(5f)
        .build()
```

## Result

![Screenshot_20230723_173228](https://github.com/RGregat/Spirit-Level-Custom-View/assets/3974162/f3831711-7034-4e14-90c0-0885b46054ec)
