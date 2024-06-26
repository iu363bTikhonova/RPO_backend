import React from 'react';
import { Navbar, Nav } from 'react-bootstrap'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import {faBars, faHome, faUser} from "@fortawesome/free-solid-svg-icons";
import {Link} from 'react-router-dom'
import { useNavigate } from 'react-router-dom';
import Utils from "/home/user_1/AndroidStudioProjects/backend/front/src/Utils.jsx";
import BackendService from "/home/user_1/AndroidStudioProjects/backend/front/src/services/BackendService.jsx";


class NavigationBarClass extends React.Component {

    constructor(props) {
        super(props);
        this.goHome = this.goHome.bind(this);
        this.logout = this.logout.bind(this);
    }

    goHome() {
        this.props.navigate('Home');
    }


    logout() {
        BackendService.logout().then(() => {
            Utils.removeUser();
            this.goHome()
        });
    }



    render() {
        let uname = Utils.getUserName();
        return (
            <Navbar bg="light" expand="lg">
                <button type="button"
                                            className="btn btn-outline-secondary mr-2"
                                            onClick={this.props.toggleSideBar}>
                                        <FontAwesomeIcon icon={ faBars } />
                                </button>
                <Navbar.Toggle aria-controls="basic-navbar-nav" />
                <Navbar.Collapse id="basic-navbar-nav">
                    <Nav className="me-auto">
                        <Nav.Link as={Link} to="/home">Home</Nav.Link>
                        <Nav.Link onClick={this.goHome}>Another Home</Nav.Link>
                        <Nav.Link onClick={() =>{ this.props.navigate("/home")}}>Yet Another Home</Nav.Link>
                    </Nav>
                    <Navbar.Text>{uname}</Navbar.Text>
                    { uname &&
                        <Nav.Link onClick={this.logout}><FontAwesomeIcon icon={faUser} fixedWidth />{' '}Выход</Nav.Link>
                    }
                    { !uname &&
                        <Nav.Link as={Link} to="/login"><FontAwesomeIcon icon={faUser} fixedWidth />{' '}Вход</Nav.Link>
                    }
                </Navbar.Collapse>
            </Navbar>
        );
    }

}

const NavigationBar = props => {
    const navigate = useNavigate()

    return <NavigationBarClass navigate={navigate} {...props} />
}


export default  NavigationBar;