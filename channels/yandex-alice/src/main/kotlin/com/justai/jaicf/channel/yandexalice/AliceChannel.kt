package com.justai.jaicf.channel.yandexalice

import com.justai.jaicf.api.BotApi
import com.justai.jaicf.channel.jaicp.JaicpCompatibleBotChannel
import com.justai.jaicf.channel.jaicp.JaicpCompatibleChannelFactory
import com.justai.jaicf.channel.yandexalice.api.AliceApi
import com.justai.jaicf.channel.yandexalice.api.AliceBotRequest
import com.justai.jaicf.channel.yandexalice.api.AliceBotResponse
import com.justai.jaicf.context.RequestContext

class AliceChannel(
    override val botApi: BotApi,
    private val oauthToken: String? = null
) : JaicpCompatibleBotChannel {

    override fun process(input: String): String? {
        val request = JSON.parse(AliceBotRequest.serializer(), input)
        val response = AliceBotResponse(request)

        if (request.request.originalUtterance == "ping") {
            response.response.text = "pong"
        } else {
            val api = oauthToken?.let { AliceApi(oauthToken, request.session.skillId) }
            val reactions = AliceReactions(api, request, response)
            botApi.process(request, reactions, RequestContext(newSession = request.session.newSession))
        }

        return JSON.stringify(AliceBotResponse.serializer(), response)
    }

    class Factory(
        private val oauthToken: String? = null
    ) : JaicpCompatibleChannelFactory {
        override val channelType = "yandex"
        override fun create(botApi: BotApi) = AliceChannel(botApi, oauthToken)
    }
}