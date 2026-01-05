# GuardianX Backend API

A Ktor-based backend for the GuardianX personal safety application, using MongoDB for data storage.

## 🛠️ Tech Stack

- **Ktor** - Kotlin async web framework
- **MongoDB** - NoSQL database (via KMongo)
- **JWT** - Authentication
- **BCrypt** - Password hashing
- **JavaMail** - Email via Gmail SMTP

## 📁 Project Structure

```
server/
├── src/main/kotlin/org/example/project/
│   ├── Application.kt              # Main entry point
│   ├── data/
│   │   ├── database/
│   │   │   └── MongoDB.kt          # MongoDB connection
│   │   ├── models/                 # Data models
│   │   │   ├── User.kt
│   │   │   ├── EmergencyContact.kt
│   │   │   ├── PoliceStation.kt
│   │   │   ├── Trip.kt
│   │   │   ├── SOSAlert.kt
│   │   │   ├── CommunityPost.kt
│   │   │   └── AuthModels.kt
│   │   └── repository/             # Database operations
│   │       ├── UserRepository.kt
│   │       ├── EmergencyContactRepository.kt
│   │       ├── PoliceStationRepository.kt
│   │       ├── TripRepository.kt
│   │       ├── SOSAlertRepository.kt
│   │       └── CommunityRepository.kt
│   ├── plugins/                    # Ktor plugins
│   │   ├── Serialization.kt
│   │   ├── Security.kt
│   │   ├── CORS.kt
│   │   ├── StatusPages.kt
│   │   ├── Monitoring.kt
│   │   ├── Databases.kt
│   │   └── Routing.kt
│   ├── routes/                     # API routes
│   │   ├── AuthRoutes.kt
│   │   ├── UserRoutes.kt
│   │   ├── EmergencyContactRoutes.kt
│   │   ├── PoliceStationRoutes.kt
│   │   ├── TripRoutes.kt
│   │   ├── SOSRoutes.kt
│   │   └── CommunityRoutes.kt
│   ├── services/
│   │   └── EmailService.kt         # Gmail SMTP service
│   └── utils/
│       ├── ApiException.kt
│       └── PasswordHasher.kt
└── src/main/resources/
    ├── application.yaml            # Configuration
    └── logback.xml                 # Logging config
```

## 🚀 Setup Instructions

### 1. Install MongoDB

```bash
# macOS
brew install mongodb-community
brew services start mongodb-community

# Ubuntu
sudo apt-get install mongodb
sudo systemctl start mongodb

# Or use MongoDB Atlas (cloud)
```

### 2. Configure Gmail SMTP

1. Go to Google Account settings
2. Enable 2-Factor Authentication
3. Generate App Password:
   - Go to Security → App passwords
   - Select "Mail" and your device
   - Copy the 16-character password
4. Update `application.yaml` with your credentials

### 3. Update Configuration

Edit `src/main/resources/application.yaml`:

```yaml
jwt:
  secret: "your-super-secret-key-change-in-production"  # Change this!

mongodb:
  connectionString: "mongodb://localhost:27017"  # Or your MongoDB Atlas URI
  database: "guardianx"

smtp:
  host: "smtp.gmail.com"
  port: 587
  username: "your-email@gmail.com"        # Your Gmail
  password: "your-app-password"           # 16-char app password
  fromEmail: "noreply@guardianx.com"
  fromName: "GuardianX Safety"
```

### 4. Run the Server

```bash
./gradlew :server:run
```

Server starts at: `http://localhost:8080`

## 📚 API Endpoints

### Authentication (`/api/v1/auth`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/register` | Register new user |
| POST | `/login` | Login user |
| POST | `/verify-email` | Verify email with 6-digit code |
| POST | `/resend-verification` | Resend verification code |
| POST | `/forgot-password` | Request password reset |
| POST | `/verify-reset-code` | Verify reset code |
| POST | `/reset-password` | Reset password with code |

### User Profile (`/api/v1/user`) 🔒

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/profile` | Get current user profile |
| PUT | `/profile` | Update profile |
| POST | `/location` | Update location |
| POST | `/change-password` | Change password |
| POST | `/device-token` | Update push notification token |
| DELETE | `/account` | Delete account |

### Emergency Contacts (`/api/v1/emergency-contacts`) 🔒

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/` | Get all contacts |
| GET | `/{id}` | Get single contact |
| POST | `/` | Add new contact |
| PUT | `/{id}` | Update contact |
| PATCH | `/{id}/toggle-active` | Toggle active status |
| DELETE | `/{id}` | Delete contact |

### Police Stations (`/api/v1/police-stations`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/` | Get all stations (paginated) |
| GET | `/search?q=query` | Search stations |
| GET | `/nearby?lat=&lng=&radius=` | Get nearby stations |
| GET | `/nearest?lat=&lng=` | Get nearest station |
| GET | `/state/{state}` | Get stations by state |
| GET | `/{id}` | Get single station |
| POST | `/` 🔒 | Add new station (user contribution) |
| PUT | `/{id}` 🔒 | Update station (own only) |

### Trips (`/api/v1/trips`) 🔒

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/` | Get trip history |
| GET | `/active` | Get active trip |
| GET | `/{id}` | Get trip details |
| POST | `/start` | Start new trip |
| POST | `/{id}/check-in` | Check in during trip |
| POST | `/{id}/extend` | Extend trip duration |
| POST | `/{id}/end` | End trip safely |
| POST | `/{id}/cancel` | Cancel trip |

### SOS Alerts (`/api/v1/sos`) 🔒

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/` | Get SOS history |
| GET | `/active` | Get active alert |
| GET | `/{id}` | Get alert details |
| POST | `/trigger` | Trigger SOS alert |
| POST | `/{id}/resolve` | Resolve alert |
| POST | `/{id}/cancel` | Cancel (within 30s) |

### Community (`/api/v1/community`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/posts` | Get all posts |
| GET | `/posts/{id}` | Get single post |
| GET | `/posts/{id}/comments` | Get post comments |
| GET | `/posts/feed` 🔒 | Get posts with like status |
| GET | `/posts/my` 🔒 | Get own posts |
| POST | `/posts` 🔒 | Create post |
| PUT | `/posts/{id}` 🔒 | Update post |
| DELETE | `/posts/{id}` 🔒 | Delete post |
| POST | `/posts/{id}/like` 🔒 | Like post |
| DELETE | `/posts/{id}/like` 🔒 | Unlike post |
| POST | `/posts/{id}/comments` 🔒 | Add comment |
| DELETE | `/posts/{postId}/comments/{commentId}` 🔒 | Delete comment |
| POST | `/comments/{id}/like` 🔒 | Like comment |
| DELETE | `/comments/{id}/like` 🔒 | Unlike comment |

🔒 = Requires JWT Authentication

## 🔐 Authentication

Include JWT token in the `Authorization` header:

```
Authorization: Bearer <your-jwt-token>
```

## 📝 Request/Response Examples

### Register User

```bash
POST /api/v1/auth/register
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "SecurePass123",
  "fullName": "John Doe",
  "phoneNumber": "+2348012345678"
}
```

### Trigger SOS Alert

```bash
POST /api/v1/sos/trigger
Authorization: Bearer <token>
Content-Type: application/json

{
  "latitude": 6.5244,
  "longitude": 3.3792,
  "address": "Lagos, Nigeria",
  "action": "EMAIL_CONTACTS",
  "message": "I need help!"
}
```

### Start Trip

```bash
POST /api/v1/trips/start
Authorization: Bearer <token>
Content-Type: application/json

{
  "startLatitude": 6.5244,
  "startLongitude": 3.3792,
  "startAddress": "Home",
  "destLatitude": 6.4281,
  "destLongitude": 3.4219,
  "destAddress": "Office",
  "expectedDurationMinutes": 30,
  "checkInIntervalMinutes": 5,
  "guardianIds": ["contact-id-1", "contact-id-2"]
}
```

## 🗃️ MongoDB Collections

- `user` - User accounts
- `emergencyContact` - Emergency contacts
- `policeStation` - Police station database
- `trip` - Trip monitoring records
- `sOSAlert` - SOS alert history
- `communityPost` - Community posts
- `postComment` - Post comments

## 🔧 Environment Variables (Alternative to YAML)

```bash
JWT_SECRET=your-secret-key
MONGODB_URI=mongodb://localhost:27017
MONGODB_DATABASE=guardianx
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
SMTP_USERNAME=your-email@gmail.com
SMTP_PASSWORD=your-app-password
```

## 📱 Mobile Integration

### Getting Location (Android/iOS)

The mobile app should:
1. Request location permissions
2. Get coordinates from device GPS
3. Optionally use Google Geocoding API for addresses
4. Send coordinates to backend APIs

### Panic Mode Flow

1. User presses SOS button
2. App gets current location from device
3. App calls `POST /api/v1/sos/trigger`
4. Backend:
   - Finds nearest police station
   - Sends emails to emergency contacts
   - Returns police station phone number
5. App can then initiate phone call to police

## 🚧 Future Enhancements

- [ ] WebSocket for real-time trip tracking
- [ ] Push notifications (Firebase)
- [ ] SMS integration (Twilio/AfricasTalking)
- [ ] Admin dashboard
- [ ] Rate limiting
- [ ] File upload for profile images
- [ ] Background trip monitoring job

## 📄 License

MIT License - GuardianX Safety Application
