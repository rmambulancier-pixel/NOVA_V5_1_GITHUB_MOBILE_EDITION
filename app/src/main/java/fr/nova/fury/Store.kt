package fr.nova.fury

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.json.JSONArray
import org.json.JSONObject

private val Context.dataStore by preferencesDataStore("nova_v5")

class NovaStore(private val context: Context) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private object K {
        val state = stringPreferencesKey("state")
    }

    val flow: Flow<NovaState> = context.dataStore.data.map {
        it[K.state]?.let(::decode) ?: NovaState()
    }

    fun save(state: NovaState) {
        scope.launch {
            context.dataStore.edit { it[K.state] = encode(state) }
        }
    }

    private fun encode(s: NovaState): String = JSONObject().apply {
        put("dark", s.dark)
        put("bio", s.biometricLock)
        put("brain", JSONArray(s.brain))

        put("missions", JSONArray().apply {
            s.missions.forEach { m ->
                put(JSONObject().apply {
                    put("id",m.id); put("title",m.title); put("area",m.area.name)
                    put("impact",m.impact); put("effort",m.effort); put("urgency",m.urgency); put("done",m.done)
                })
            }
        })

        put("deals", JSONArray().apply {
            s.deals.forEach { d ->
                put(JSONObject().apply {
                    put("id",d.id); put("title",d.title); put("buy",d.buy); put("sell",d.sell)
                    put("fees",d.fees); put("shipping",d.shipping); put("risk",d.risk)
                    put("confidence",d.confidence); put("demand",d.demand)
                })
            }
        })

        put("watches", JSONArray().apply {
            s.watches.forEach { w ->
                put(JSONObject().apply {
                    put("id",w.id); put("name",w.name); put("movement",w.movement)
                    put("case",w.caseDesign); put("dial",w.dial); put("price",w.targetPrice)
                    put("cost",w.estimatedCost); put("originality",w.originality)
                    put("fit",w.brandFit); put("feasibility",w.feasibility)
                })
            }
        })

        put("cash", JSONArray().apply {
            s.cash.forEach { c ->
                put(JSONObject().apply {
                    put("id",c.id); put("title",c.title); put("amount",c.amount)
                    put("income",c.income); put("category",c.category)
                })
            }
        })
    }.toString()

    private fun decode(raw: String): NovaState {
        val o = JSONObject(raw)

        fun arr(name:String) = o.optJSONArray(name) ?: JSONArray()

        val missions = arr("missions").let { a ->
            List(a.length()) { i ->
                val x=a.getJSONObject(i)
                Mission(
                    id=x.getString("id"),
                    title=x.getString("title"),
                    area=Area.valueOf(x.getString("area")),
                    impact=x.getInt("impact"),
                    effort=x.getInt("effort"),
                    urgency=x.getInt("urgency"),
                    done=x.getBoolean("done")
                )
            }
        }

        val deals = arr("deals").let { a ->
            List(a.length()) { i ->
                val x=a.getJSONObject(i)
                Deal(
                    id=x.getString("id"), title=x.getString("title"),
                    buy=x.getDouble("buy"), sell=x.getDouble("sell"),
                    fees=x.optDouble("fees"), shipping=x.optDouble("shipping"),
                    risk=x.optInt("risk",30), confidence=x.optInt("confidence",70),
                    demand=x.optInt("demand",70)
                )
            }
        }

        val watches = arr("watches").let { a ->
            List(a.length()) { i ->
                val x=a.getJSONObject(i)
                WatchConcept(
                    id=x.getString("id"), name=x.getString("name"),
                    movement=x.getString("movement"), caseDesign=x.getString("case"),
                    dial=x.getString("dial"), targetPrice=x.getDouble("price"),
                    estimatedCost=x.getDouble("cost"), originality=x.getInt("originality"),
                    brandFit=x.getInt("fit"), feasibility=x.getInt("feasibility")
                )
            }
        }

        val cash = arr("cash").let { a ->
            List(a.length()) { i ->
                val x=a.getJSONObject(i)
                CashFlow(
                    id=x.getString("id"), title=x.getString("title"),
                    amount=x.getDouble("amount"), income=x.getBoolean("income"),
                    category=x.getString("category")
                )
            }
        }

        val brain = arr("brain").let { a -> List(a.length()) { a.getString(it) } }

        return NovaState(
            missions = if (missions.isEmpty()) NovaState().missions else missions,
            deals = deals,
            watches = watches,
            cash = cash,
            brain = if (brain.isEmpty()) NovaState().brain else brain,
            dark = o.optBoolean("dark", true),
            biometricLock = o.optBoolean("bio", false)
        )
    }
}
