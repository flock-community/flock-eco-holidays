import {useEffect, useState} from "react"
import {PersonClient} from "../clients/PersonClient"
import {useLoginStatus} from "./StatusHook"

let store

export function usePerson() {
  const status = useLoginStatus()

  const [state, setState] = useState(store)

  useEffect(() => {
    if (store === undefined && status && status.loggedIn) {
      store = null
      PersonClient.me().then(it => {
        store = it
        setState(store)
      })
    } else {
      setState(store)
    }
  }, [status])

  const handlePerson = personCode => {
    PersonClient.get(personCode).then(it => {
      store = it
      setState(store)
    })
  }

  return [state, handlePerson]
}
