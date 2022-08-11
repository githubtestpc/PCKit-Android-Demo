package com.pcsfg.pckit.chatkit.model

enum class ChatMessageKind {

    Text,
    Image,
    ImageText,
    Location,
    Contact,
    QuickReply,
    Carousel,
    Video,
    Document,
    Loading,
    DateTime,
    SystemMessage

}

enum class CachedMessageKind {

    Text,
    ImageText,
    File
}