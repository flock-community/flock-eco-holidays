import React, {useContext, useEffect, useState} from "react"

import Fab from "@material-ui/core/Fab"
import AddIcon from "@material-ui/icons/Add"
import {makeStyles} from "@material-ui/core/styles"
import Grid from "@material-ui/core/Grid"
import {SickdayDialog} from "./SickdayDialog"
import {SickdayList} from "./SickdayList"
import {UserSelector} from "../../components/UserSelector"
import SickdayClient from "./SickdayClient"
import {ApplicationContext} from "../../application/ApplicationContext"
import {isDefined} from "../../utils/validation"

const useStyles = makeStyles({
  root: {
    padding: 20,
  },
  fab: {
    position: "absolute",
    bottom: "25px",
    right: "25px",
  },
})

/**
 * @return {null}
 */
export function SickdayFeature() {
  const classes = useStyles()

  const [reload, setReload] = useState(false)
  const [open, setOpen] = useState(false)
  const [value, setValue] = useState(null)
  const [userCode, setUserCode] = useState(null)
  const [users, setUsers] = useState([])
  const {authorities, user} = useContext(ApplicationContext)

  function isSuperUser() {
    return authorities && authorities.includes("SickdayAuthority.ADMIN")
  }

  useEffect(() => {
    if (isSuperUser()) {
      // eslint-disable-next-line no-shadow
      SickdayClient.getAllUsers().then(users => {
        setUsers(users)
      })
    }

    if (isDefined(user)) setUserCode(user.code)
  }, [authorities, user])

  function handleCompleteDialog() {
    setReload(reload)
    setOpen(false)
    setValue(null)
  }

  function handleClickAdd() {
    setValue(null)
    setOpen(true)
  }

  function handleClickRow(item) {
    setValue({
      ...item,
      days: item.days.map(it => it.hours),
      type: item.days[0].type,
    })

    setOpen(true)
  }

  // eslint-disable-next-line no-shadow
  function handleChangeUser(user) {
    if (isDefined(user)) setUserCode(user.code)
  }

  if (!userCode) {
    return null
  }

  return (
    <div className={classes.root}>
      <Grid container spacing={1}>
        <Grid item xs={12}>
          {isSuperUser() && <UserSelector users={users} onChange={handleChangeUser} />}
        </Grid>
        <Grid item xs={12}>
          <SickdayList
            userCode={userCode}
            onClickRow={handleClickRow}
          />
              refresh={reload}
        </Grid>
      </Grid>
      <SickdayDialog
        open={open}
        userCode={userCode}
        value={value}
        onComplete={handleCompleteDialog}
      />

      <Fab color="primary" className={classes.fab} onClick={handleClickAdd}>
        <AddIcon />
      </Fab>
    </div>
  )
}

SickdayFeature.propTypes = {}
