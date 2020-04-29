import React, {useContext, useState} from "react"

import {makeStyles} from "@material-ui/core/styles"
import Grid from "@material-ui/core/Grid"
import {Container} from "@material-ui/core"
import {SickDayDialog} from "./SickDayDialog"
import {SickDayList} from "./SickDayList"
import {PersonSelector} from "../../components/selector"
import {ApplicationContext} from "../../application/ApplicationContext"
import {AddActionFab} from "../../components/FabButtons"
import {usePerson} from "../../hooks/PersonHook"

const useStyles = makeStyles({
  root: {
    padding: 20,
  },
})

/**
 * @return {null}
 */
export function SickDayFeature() {
  const classes = useStyles()

  const [person, setPerson] = usePerson()

  const [reload, setReload] = useState(false)
  const [open, setOpen] = useState(false)
  const [value, setValue] = useState(null)
  const {authorities} = useContext(ApplicationContext)

  function isSuperUser() {
    return authorities && authorities.includes("SickdayAuthority.ADMIN")
  }

  function handleCompleteDialog() {
    setReload(!reload)
    setOpen(false)
    setValue(null)
  }

  function handleClickAdd() {
    setValue(null)
    setOpen(true)
  }

  function handleClickRow(item) {
    setValue(item)
    setOpen(true)
  }

  function handlePersonChange(it) {
    setPerson(it)
  }

  return (
    <Container className={classes.root}>
      <Grid container spacing={1}>
        <Grid item xs={12}>
          {isSuperUser() && (
            <PersonSelector
              value={person && person.code}
              onChange={handlePersonChange}
            />
          )}
        </Grid>
        <Grid item xs={12}>
          <SickDayList
            personCode={person && person.code}
            onClickRow={handleClickRow}
            refresh={reload}
          />
        </Grid>
      </Grid>
      <SickDayDialog
        open={open}
        code={value && value.code}
        personCode={person && person.code}
        value={value}
        onComplete={handleCompleteDialog}
      />

      <AddActionFab color="primary" onClick={handleClickAdd} />
    </Container>
  )
}

SickDayFeature.propTypes = {}
