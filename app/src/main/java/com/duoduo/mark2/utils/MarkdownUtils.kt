package com.duoduo.mark2.utils

import android.content.Context
import android.text.Spannable
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import io.noties.markwon.*
import io.noties.markwon.core.spans.LinkSpan
import io.noties.markwon.image.AsyncDrawableSpan
import io.noties.markwon.image.ImageProps
import io.noties.markwon.image.ImagesPlugin
import org.commonmark.node.Image
import org.commonmark.node.Node
import org.commonmark.node.Text
import java.util.Objects


class MarkdownUtils {

    interface ImageClickListener {
        fun onClick(url: String)
    }

    companion object {
        @JvmStatic
        fun newMarkwon(context: Context, listener: ImageClickListener): Markwon {
            return Markwon.builder(context)
                .usePlugin(object : AbstractMarkwonPlugin() {
                    override fun configureVisitor(builder: MarkwonVisitor.Builder) {
                        builder.on(
                            Image::class.java
                        ) { visitor, image ->
                            val start = visitor.builder().length
                            visitor.visitChildren(image)
                            val factory = visitor.configuration().spansFactory().get(image.javaClass)
                            val renderProps = visitor.renderProps()
                            renderProps.set(ImageProps.DESTINATION, image.destination)
                            visitor.setSpans(start, factory?.getSpans(visitor.configuration(), renderProps))
                        }
                        builder.on(
                            Text::class.java
                        ) { visitor, text ->
                            visitor.visitChildren(text)
                            val str = text.literal
                            val append = if (text.parent.firstChild is Image && text.literal.trim().isNotEmpty()) {
                                "\n"
                            } else {
                                ""
                            }
                            visitor.builder().append(
                               append + str + append + append
                            )
                        }
                    }
                })
                .usePlugin(ImagesPlugin.create())
                /*
                .usePlugin(GlideImagesPlugin.create(object : GlideImagesPlugin.GlideStore {
                    override fun load(drawable: AsyncDrawable): RequestBuilder<Drawable> {
                        return Glide
                            .with(context)
                            .load(drawable.destination)
                            .transition(DrawableTransitionOptions.withCrossFade(500))
                    }

                    override fun cancel(target: Target<*>) {
                        Glide.with(context).clear(target);
                    }
                }))*/
                .usePlugin(object : AbstractMarkwonPlugin() {
                    override fun configureSpansFactory(builder: MarkwonSpansFactory.Builder) {
                        builder.appendFactory(Image::class.java) { configuration, props ->

                            val url = ImageProps.DESTINATION.require(props)

                            LinkSpan(
                                configuration.theme(),
                                url,
                                ImageLinkResolver(configuration.linkResolver(), listener)
                            )
                        }
                    }
                })
                .build()
        }
    }

    class ImageLinkResolver(val original: LinkResolver, val listener: ImageClickListener) : LinkResolver {
        override fun resolve(view: View, link: String) {
            listener.onClick(link)
            // original.resolve(view, link)
        }
    }
}

fun Spanned.fixMarkdownImageLabel() {
    val spans = this.getSpans(0, this.length, Any::class.java)
    for (span in spans) {
        Log.d("MarkdownImageLabelFix", span.toString())
    }
}