var currentData;

function filterByCovidStatus() {
    var covidStatus = document.getElementById('covid-status').value;
    var searchRes = '';
    if (covidStatus == "positive") {
        for (record of currentData) {
            if (record.isAffected) {
                searchRes = searchRes + '<tr><td>' + record.country + '</td><td>' + record.name + '</td><td>' + record.age + '</td><td id="isAffected-' + record.id + '">Yes</td><td><button class="btn btn-info" onclick="flipCovidStatus(' + record.id + ')">Flip Covid Status</button></td></tr>';
            }
        }
    }

    if (covidStatus == "negative") {
        for (record of currentData) {
            if (!record.isAffected) {
                searchRes = searchRes + '<tr><td>' + record.country + '</td><td>' + record.name + '</td><td>' + record.age + '</td><td id="isAffected-' + record.id + '">No</td><td><button class="btn btn-info" onclick="flipCovidStatus(' + record.id + ')">Flip Covid Status</button></td></tr>';
            }
        }
    }
    document.getElementById("table-body").innerHTML = searchRes;
}

function filterByAge() {
    var comparision = document.getElementById('comparision').value;
    var ageValue = document.getElementById('age-drop').value;
    var searchRes = '';
    if (comparision == "greater") {
        for (record of currentData) {
            var ageGreater = record.age;
            if (ageGreater > ageValue) {
                var affected = '' ;
                if (record.isAffected) {
                    affected = 'Yes'
                } else {
                    affected = 'No'
                }
                searchRes = searchRes + '<tr><td>' + record.country + '</td><td>' + record.name + '</td><td>' + record.age + '</td><td id="isAffected-' + record.id + '">' + affected + '</td><td><button class="btn btn-info" onclick="flipCovidStatus(' + record.id + ')">Flip Covid Status</button></td></tr>';
            }
        }
    }

    if (comparision == "less") {
        for (record of currentData) {
            var age = record.age;
            if (age < ageValue) {
                var affected = '' ;
                if (record.isAffected) {
                    affected = 'Yes'
                } else {
                    affected = 'No'
                }
                searchRes = searchRes + '<tr><td>' + record.country + '</td><td>' + record.name + '</td><td>' + record.age + '</td><td id="isAffected-' + record.id + '">' + affected + '</td><td><button class="btn btn-info" onclick="flipCovidStatus(' + record.id + ')">Flip Covid Status</button></td></tr>';
            }
        }
    }

    document.getElementById("table-body").innerHTML = searchRes;
}

function myFunction(id) {
    var keyword = document.getElementById(id).value;
    var searchRes = '';
    for (record of currentData) {
        var name = record.name;
        if (name.toLowerCase().includes(keyword.toLowerCase())) {
            var affected = '' ;
            if (record.isAffected) {
                affected = 'Yes'
            } else {
                affected = 'No'
            }
            searchRes = searchRes + '<tr><td>' + record.country + '</td><td>' + record.name + '</td><td>' + record.age + '</td><td id="isAffected-' + record.id + '">' + affected + '</td><td><button class="btn btn-info" onclick="flipCovidStatus(' + record.id + ')">Flip Covid Status</button></td></tr>';
        }
    }

    document.getElementById("table-body").innerHTML = searchRes;
}

$("#addCitizen").click(function (e) {
    e.preventDefault();

    var csrfToken = document.getElementsByName("csrfToken")[0].value;
    var formdata = new FormData();
    formdata.append("csrfToken", csrfToken);
    formdata.append("country", $("#country").val());
    formdata.append("name", $("#name").val());
    formdata.append("age", $("#age").val());
    formdata.append("isAffected", $("input[name='isAffected']:checked").val());

    jsRoutes.controllers.Application.addCitizen().ajax({
        type: "POST",
        processData: false,
        contentType: false,
        data: formdata,
        beforeSend: function (request) {
            return request.setRequestHeader('csrfToken', csrfToken);
        },
        success: function (data) {
            $('#myModal').modal('hide');
            var obj = JSON.parse(JSON.stringify(data));
            var message = '<div class="alert alert-success alert-dismissible fade show" role="alert"><strong>Success! </strong>' + obj.message + '<button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button></div>';
            document.getElementById("cust-message").innerHTML = message;
            document.getElementById("cust-message-pop").innerHTML = '';
            loadData("all");
            loadCountries();
        },
        error: function (er) {
            var obj = JSON.parse(JSON.stringify(er));
            var errMessage = '<div class="alert alert-danger alert-dismissible fade show" role="alert"><strong>OOps! </strong>Please provide valid values!<button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button></div>';
            document.getElementById("cust-message-pop").innerHTML = errMessage;
        }
    });
});


function loadCountries() {
    jsRoutes.controllers.Application.getCountries().ajax({
        type: "GET",
        processData: false,
        contentType: false,
        success: function (data) {
            var countries = JSON.parse(JSON.stringify(data));
            var countryRes = '<option value="all">Country</option>';
            for (country of countries) {
                countryRes = countryRes + '<option value="' + country + '">' + country + '</option>';
            }
            document.getElementById("country-select").innerHTML = countryRes;
        },
        error: function (er) {
            var obj = JSON.parse(JSON.stringify(er));
            var errMessage = '<div class="alert alert-danger alert-dismissible fade show" role="alert"><strong>OOps! </strong>' + obj.message + '<button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button></div>';
            document.getElementById("cust-message").innerHTML = errMessage;
        }
    });
}

loadCountries();

function loadData(country) {
    jsRoutes.controllers.Application.getByCountry(country).ajax({
        type: "GET",
        processData: false,
        contentType: false,
        success: function (data) {
            var records = JSON.parse(JSON.stringify(data));
            var recordsRes = '';
            for (record of records) {
                var affected = '' ;
                if (record.isAffected == true) {
                    affected = 'Yes'
                } else {
                    affected = 'No'
                }
                recordsRes = recordsRes + '<tr><td>' + record.country + '</td><td>' + record.name + '</td><td>' + record.age + '</td><td id="isAffected-' + record.id + '">' + affected + '</td><td><button class="btn btn-info"  onclick="flipCovidStatus(' + record.id + ')">Flip Covid Status</button></td></tr>';
            }

            currentData = records;
            document.getElementById("table-body").innerHTML = recordsRes;
        },
        error: function (er) {
            var obj = JSON.parse(JSON.stringify(er));
            var errMessage = '<div class="alert alert-danger alert-dismissible fade show" role="alert"><strong>OOps! </strong>' + obj.message + '<button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button></div>';
            document.getElementById("cust-message").innerHTML = errMessage;
        }
    });
}

loadData("all");

$('#country-select').on('change', function () {
    loadData(this.value);
    document.getElementById('myInput').value = '';
});

function flipCovidStatus(id) {
    jsRoutes.controllers.Application.flipStatus(id).ajax({
        type: "GET",
        processData: false,
        contentType: false,
        success: function (_) {
            var currentValue = document.getElementById("isAffected-" + id).innerHTML;
            if (currentValue == "Yes") {
                document.getElementById("isAffected-" + id).innerHTML = "No"
            } else {
                document.getElementById("isAffected-" + id).innerHTML = "Yes"
            }
        },
        error: function (er) {
            var obj = JSON.parse(JSON.stringify(er));
            var errMessage = '<div class="alert alert-danger alert-dismissible fade show" role="alert"><strong>OOps! </strong>' + obj.message + '<button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button></div>';
            document.getElementById("cust-message").innerHTML = errMessage;
        }
    });
}