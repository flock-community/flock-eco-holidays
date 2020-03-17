import React, {useEffect, useState} from "react"
import PropTypes from "prop-types"
import * as Yup from "yup"
import {Field, Form, Formik} from "formik"
import {TextField} from "formik-material-ui"
import Grid from "@material-ui/core/Grid"
import MenuItem from "@material-ui/core/MenuItem"
import UserAuthorityUtil from "@flock-eco/feature-user/src/main/react/user_utils/UserAuthorityUtil"
import {MuiPickersUtilsProvider} from "@material-ui/pickers"
import MomentUtils from "@date-io/moment"
import moment from "moment"
import {HolidayClient} from "../../clients/HolidayClient"
import {isDefined} from "../../utils/validation"
import {DatePickerField} from "../../components/fields/DatePickerField"
import {PeriodInputField} from "../../components/fields/PeriodInputField"

export const HOLIDAY_FORM_ID = "holiday-form-id"

const now = moment()

const schema = Yup.object().shape({
  description: Yup.string()
    .required("Field required")
    .default(""),
  status: Yup.string()
    .required("Field required")
    .default("REQUESTED"),
  from: Yup.date()
    .required("From date is required")
    .default(now),
  to: Yup.date()
    .required("To date is required")
    .default(now),
  days: Yup.array().required("Required"),
})

export function HolidayForm({code, onSubmit}) {
  const [state, setState] = useState(null)

  useEffect(() => {
    if (code) {
      HolidayClient.get(code).then(res => {
        setState({
          description: res.description,
          status: res.status,
          from: res.from,
          to: res.to,
          days: res.days,
        })
      })
    } else {
      setState(schema.cast())
    }
  }, [code])

  const handleSubmit = value => {
    if (isDefined(onSubmit)) onSubmit(value)
  }

  const handleChange = it => {
    setState(it)
  }

  const renderForm = () => (
    <Form id={HOLIDAY_FORM_ID}>
      <MuiPickersUtilsProvider utils={MomentUtils}>
        <Grid container spacing={1}>
          <Grid item xs={12}>
            <Field
              name="description"
              type="text"
              label="Description"
              fullWidth
              component={TextField}
            />
          </Grid>

          {code && (
            <Grid item xs={12}>
              <UserAuthorityUtil has={"HolidayAuthority.ADMIN"}>
                <Field
                  fullWidth
                  type="text"
                  name="status"
                  label="Status"
                  select
                  variant="standard"
                  margin="normal"
                  component={TextField}
                >
                  <MenuItem value="REQUESTED">REQUESTED</MenuItem>
                  <MenuItem value="APPROVED">APPROVED</MenuItem>
                  <MenuItem value="REJECTED">REJECTED</MenuItem>
                </Field>
              </UserAuthorityUtil>
            </Grid>
          )}
          <Grid item xs={6}>
            <DatePickerField name="from" label="From" fullWidth />
          </Grid>
          <Grid item xs={6}>
            <DatePickerField label="To" name="to" fullWidth />
          </Grid>
          <Grid item xs={12}>
            <PeriodInputField
              name="days"
              from={state && state.from}
              to={state && state.to}
            />
          </Grid>
        </Grid>
      </MuiPickersUtilsProvider>
    </Form>
  )

  return (
    state && (
      <Formik
        enableReinitialize
        initialValues={state}
        onSubmit={handleSubmit}
        validationSchema={schema}
        validate={handleChange}
        render={renderForm}
      />
    )
  )
}

HolidayForm.propTypes = {
  code: PropTypes.string,
  onSubmit: PropTypes.func,
}
