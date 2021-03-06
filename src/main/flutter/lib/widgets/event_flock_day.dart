import 'dart:convert';

import 'package:flutter/material.dart';

import '../app.dart';

class EventFlockDay extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return FutureBuilder<FlockDay>(
      future: fetchData(),
      builder: (context, snapshot) {
        if (snapshot.hasData) {
          return Center(
            child: () {
              if (snapshot.data.today) {
                return Text.rich(
                  TextSpan(
                    children: <TextSpan>[
                      TextSpan(
                        text: 'Today ',
                        style: TextStyle(fontSize: 24, fontWeight: FontWeight.bold),
                      ),
                      TextSpan(
                        text: 'it is Flock. day',
                        style: TextStyle(fontSize: 18),
                      )
                    ],
                  ),
                );
              } else {
                return Text.rich(
                  TextSpan(
                    children: <TextSpan>[
                      TextSpan(
                        text: '${snapshot.data.next} days ',
                        style: TextStyle(fontSize: 24, fontWeight: FontWeight.bold),
                      ),
                      TextSpan(
                        text: 'until the next Flock. day.',
                        style: TextStyle(fontSize: 18),
                      )
                    ],
                  ),
                );
              }
            }(),
          );
        } else if (snapshot.hasError) {
          return Text("${snapshot.error}");
        }
        // By default, show a loading spinner.
        return CircularProgressIndicator();
      },
    );
    ;
  }

  Future<FlockDay> fetchData() async {
    var response = await app.request.get('events/flock_day');
    var res = FlockDay.fromJson(json.decode(response.body));
    return res;
  }
}

class FlockDay {
  final int next;
  final bool today;

  FlockDay({this.next, this.today});

  factory FlockDay.fromJson(Map<String, dynamic> json) {
    return FlockDay(
      next: json['next'],
      today: json['today'],
    );
  }
}
